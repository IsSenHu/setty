package com.setty.discovery.core;

import com.alibaba.fastjson.JSON;
import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.cons.registry.Header;
import com.setty.commons.util.http.OkHttpUtil;
import com.setty.commons.util.result.ResultEqualUtil;
import com.setty.commons.vo.JsonResult;
import com.setty.commons.vo.registry.AppVO;
import com.setty.commons.vo.registry.LeaseInfoVO;
import com.setty.discovery.config.EnableDiscoveryConfiguration;
import com.setty.discovery.core.infs.LeaseManager;
import com.setty.discovery.model.AppDao;
import com.setty.discovery.properties.DiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 默认的租约管理器
 *
 * @author HuSen
 * create on 2019/7/5 17:04
 */
@Slf4j
public class DefaultLeaseManager implements LeaseManager<AppVO, Long, String> {

    private static final String URL_SPLIT = "/";

    private final DiscoveryProperties dp;

    private final AppDao appDao;

    public DefaultLeaseManager(DiscoveryProperties dp, AppDao appDao) {
        this.dp = dp;
        this.appDao = appDao;
    }

    @Override
    public void register(AppVO vo, int leaseDuration, boolean isReplication) {
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 获取到注册中心服务端地址
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return;
        }
        LeaseInfoVO leaseInfoVO = new LeaseInfoVO();
        leaseInfoVO.setDurationInSecs(leaseDuration);
        leaseInfoVO.setRenewalIntervalInSecs(dp.getRenewalIntervalInSecs());
        vo.setLeaseInfo(leaseInfoVO);

        serviceUrl.forEach((zone, urls) -> {
            Assert.isTrue(StringUtils.isNotBlank(urls), "serviceUrl can not be empty");
            String[] split = urls.split(",");
            for (String url : split) {
                register(vo, leaseDuration, isReplication, url);
            }
        });
    }

    private void register(AppVO vo, int leaseDuration, boolean isReplication, String url) {
        Headers headers = getPairs(isReplication);
        String body = JSON.toJSONString(vo);
        String resp = OkHttpUtil.postSync(url, body, headers);
        log.info("注册到服务中心结果:{}", resp);
        // 如果没有注册成功 放入任务队列
        JsonResult result = JSON.parseObject(resp, JsonResult.class);
        if (StringUtils.isBlank(resp) || result == null || !result.getCode().equals(JsonResultCode.SUCCESS.getCode())) {
            // 如果队列满了 会直接返回false
            log.info("服务注册失败");
            EnableDiscoveryConfiguration.RUN_QUEUE.offer(() -> register(vo, leaseDuration, isReplication));
        }
    }

    @Override
    public boolean cancel(Long id, String name, boolean isReplication) {
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 获取到注册中心服务端地址
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return true;
        }
        serviceUrl.forEach((zone, urls) -> {
            Assert.isTrue(StringUtils.isNotBlank(urls), "serviceUrl can not be empty");
            Headers headers = getPairs(isReplication);
            String[] split = urls.split(",");
            for (String url : split) {
                if (url.endsWith(URL_SPLIT)) {
                    url = url + id + URL_SPLIT + name;
                } else {
                    url = url + URL_SPLIT + id + URL_SPLIT + name;
                }
                String resp = OkHttpUtil.deleteSync(url, headers);
                log.info("服务下线结果:{}", resp);
            }
        });
        return true;
    }

    @Override
    public boolean renewal(Long id, String name, boolean isReplication) {
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 获取到注册中心服务端地址
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return true;
        }
        serviceUrl.forEach((zone, urls) -> {
            Assert.isTrue(StringUtils.isNotBlank(urls), "serviceUrl can not be empty");
            String[] split = urls.split(",");
            for (String url : split) {
                renewal(id, name, isReplication, url);
            }
        });
        return true;
    }

    private void renewal(Long id, String name, boolean isReplication, String url) {
        String temp = url;
        if (url.endsWith(URL_SPLIT)) {
            url = url + id + URL_SPLIT + name;
        } else {
            url = url + URL_SPLIT + id + URL_SPLIT + name;
        }
        Headers headers = getPairs(isReplication);
        String resp = OkHttpUtil.putSync(url, headers);
        // 如果返回404 要求重新注册
        JsonResult jsonResult = JSON.parseObject(resp, JsonResult.class);
        boolean equal = ResultEqualUtil.equal(jsonResult, JsonResultCode.NOT_FOUND);
        if (StringUtils.isNotBlank(resp) && jsonResult != null && equal) {
            AppVO vo = new AppVO();
            vo.setAppId(dp.getAppId());
            vo.setHost(dp.getHost());
            vo.setPort(dp.getPort());
            vo.setInstanceName(dp.getInstanceName());
            LeaseInfoVO leaseInfoVO = new LeaseInfoVO();
            leaseInfoVO.setDurationInSecs(dp.getLeaseDuration());
            leaseInfoVO.setRenewalIntervalInSecs(dp.getRenewalIntervalInSecs());
            vo.setLeaseInfo(leaseInfoVO);
            register(vo, dp.getLeaseDuration(), dp.getIsRegistry(), temp);
        }
    }

    @Override
    public void evit() {
        List<AppVO> all = appDao.findAll();
        all.forEach(app -> {
            LeaseInfoVO leaseInfo = app.getLeaseInfo();
            // 第一次注册时间
            Long registrationTimestamp = leaseInfo.getRegistrationTimestamp();
            // 服务续约的有效时长
            Integer durationInSecs = leaseInfo.getDurationInSecs();
            // 最后一次续约时间
            Long lastRenewalTimestamp = leaseInfo.getLastRenewalTimestamp();
            // 当前时间
            long now = System.currentTimeMillis();
            // 当前时间 - 最后一次续约时间 大于等于 有效时长 则下线该服务
            long realLast;
            if (Objects.isNull(lastRenewalTimestamp)) {
                realLast = registrationTimestamp;
            } else if (lastRenewalTimestamp < registrationTimestamp) {
                realLast = registrationTimestamp;
            } else {
                realLast = lastRenewalTimestamp;
            }
            if ((now - realLast) >= TimeUnit.SECONDS.toMillis(durationInSecs)) {
                log.info("下线服务:{}", JSON.toJSONString(app, true));
                appDao.delete(app.getAppId(), app.getInstanceName());
            }
        });
    }

    private Headers getPairs(boolean isReplication) {
        Headers headers = null;
        if (isReplication) {
            Map<String, String> isReplicationHeader = new HashMap<>(1);
            isReplicationHeader.put(Header.REGISTRY_IS_REPLICATION, "true");
            headers = Headers.of(isReplicationHeader);
        }
        return headers;
    }
}
