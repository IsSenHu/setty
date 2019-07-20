package com.setty.discovery.core;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
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
import org.apache.commons.collections4.CollectionUtils;
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

    private static final Table<Long, String, AppVO> TB_APP = HashBasedTable.create();
    private static final Table<String, Long, String> TB_URL_APP = HashBasedTable.create();

    private final DiscoveryProperties dp;

    private final AppDao appDao;

    public DefaultLeaseManager(DiscoveryProperties dp, AppDao appDao) {
        this.dp = dp;
        this.appDao = appDao;
    }

    @Override
    public void register(AppVO vo, int leaseDuration, boolean isReplication) {
        Set<String> targetUrls = getTargetUrls();
        if (CollectionUtils.isEmpty(targetUrls)) {
            log.warn("not have enable url");
        }
        targetUrls.forEach(url -> register(vo, leaseDuration, isReplication, url));
    }

    private Set<String> getTargetUrls() {
        Set<String> ret = new HashSet<>();
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 只注册到自己区域的zone上去
        String region = dp.getRegion();
        String zoneStr;
        boolean defaultRegion = StringUtils.equals(region, DiscoveryProperties.DEFAULT_REGION);
        if (defaultRegion) {
            zoneStr = DiscoveryProperties.DEFAULT_ZONE;
        } else {
            zoneStr = dp.getAvailabilityZones().get(region);
            Assert.isTrue(StringUtils.isNotBlank(zoneStr), region + "'s zone is empty");
        }
        // 获取指定zone的服务注册地址
        String[] zones = zoneStr.split(",");
        for (String zone : zones)
        {
            // 过滤掉自己
            if (StringUtils.equals(zone, dp.getZone()) && dp.getIsRegistry()) {
                continue;
            }
            String urlStr = dp.getServiceUrl().get(zone);
            Assert.isTrue(StringUtils.isNotBlank(urlStr), zone + "'s urls is empty");
            String[] urls = urlStr.split(",");
            ret.addAll(Sets.newHashSet(urls));
        }
        return ret;
    }

    private void register(AppVO vo, int leaseDuration, boolean isReplication, String url) {
        Headers headers = getPairs(isReplication);
        String body = JSON.toJSONString(vo);
        String resp = OkHttpUtil.postSync(url, body, headers);
        // 如果没有注册成功 放入任务队列
        JsonResult result = JSON.parseObject(resp, JsonResult.class);
        if (StringUtils.isBlank(resp) || result == null || !result.getCode().equals(JsonResultCode.SUCCESS.getCode())) {
            // 如果队列满了 会直接返回false
            log.warn("服务注册到:{} 失败", url);
            EnableDiscoveryConfiguration.RUN_QUEUE.offer(() -> register(vo, leaseDuration, isReplication, url));
        } else {
            log.info("服务注册到:{} 成功", url);
            TB_APP.put(vo.getAppId(), vo.getInstanceName(), vo);
            TB_URL_APP.put(url, vo.getAppId(), vo.getInstanceName());
        }
    }

    @Override
    public boolean cancel(Long id, String name, boolean isReplication) {
        Set<String> targetUrls = getTargetUrls();
        if (CollectionUtils.isEmpty(targetUrls)) {
            log.warn("not have enable url");
        }
        Headers headers = getPairs(isReplication);
        targetUrls.forEach(url -> {
            if (url.endsWith(URL_SPLIT)) {
                url = url + id + URL_SPLIT + name;
            } else {
                url = url + URL_SPLIT + id + URL_SPLIT + name;
            }
            String resp = OkHttpUtil.deleteSync(url, headers);
            log.info("服务下线结果:{}", resp);
        });
        return true;
    }

    @Override
    public boolean renewal(Long id, String name, boolean isReplication) {
        Set<String> targetUrls = getTargetUrls();
        if (CollectionUtils.isEmpty(targetUrls)) {
            log.warn("not have enable url");
        }
        targetUrls.forEach(url -> renewal(id, name, isReplication, url));
        return true;
    }

    private void renewal(Long id, String name, boolean isReplication, String url) {
        // 成功之后才能续租
        boolean contains = TB_URL_APP.contains(url, id);
        if (!contains) {
            log.warn("服务还没有注册成功，不能续约到:{}", url);
            return;
        }
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
            AppVO vo = TB_APP.get(id, name);
            if (vo != null) {
                register(vo, dp.getLeaseDuration(), dp.getIsRegistry(), temp);
            }
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
