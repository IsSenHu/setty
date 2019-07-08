package com.setty.discovery.core;

import com.alibaba.fastjson.JSON;
import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.cons.registry.Apps;
import com.setty.commons.cons.registry.Header;
import com.setty.commons.util.http.OkHttpUtil;
import com.setty.commons.vo.registry.AppVO;
import com.setty.commons.vo.registry.LeaseInfoVO;
import com.setty.discovery.config.EnableDiscoveryConfiguration;
import com.setty.discovery.core.infs.LeaseManager;
import com.setty.discovery.properties.DiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 默认的租约管理器
 *
 * @author HuSen
 * create on 2019/7/5 17:04
 */
@Slf4j
public class DefaultLeaseManager implements LeaseManager<AppVO, Long, String> {

    private final DiscoveryProperties dp;

    public DefaultLeaseManager(DiscoveryProperties dp) {
        this.dp = dp;
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

        serviceUrl.forEach((zone, url) -> {
            Assert.isTrue(StringUtils.isNotBlank(url), "serviceUrl can not be empty");
            Headers headers = getPairs(isReplication);
            String body = JSON.toJSONString(vo);
            String[] split = url.split(",");
            for (String s : split) {
                String resp = OkHttpUtil.postSync(s, body, headers);
                log.info("注册到服务中心结果:{}", resp);
                // 如果没有注册成功 放入任务队列
                RegistryJsonResult result = JSON.parseObject(resp, RegistryJsonResult.class);
                if (StringUtils.isBlank(resp) || result == null || !result.getCode().equals((Apps.REGISTRY_APP_CODE + Apps.REGISTRY_APP_CODE))) {
                    if (result != null) {
                        System.out.println(JsonResultCode.SUCCESS.getCode());
                        System.out.println(Apps.REGISTRY_APP_CODE + JsonResultCode.SUCCESS.getCode());
                        System.out.println(result.getCode());
                        System.out.println(NumberUtils.compare(result.getCode(), Apps.REGISTRY_APP_CODE + JsonResultCode.SUCCESS.getCode()));
                    }
                    // 如果队列满了 会直接返回false
                    log.info("服务注册失败");
                    EnableDiscoveryConfiguration.RUN_QUEUE.offer(() -> register(vo, leaseDuration, isReplication));
                }
                // 注册成功 可以开始续约
                else {
                    EnableDiscoveryConfiguration.RUN_QUEUE.offer(() -> renewal(vo.getAppId(), vo.getInstanceName(), isReplication));
                }
            }
        });
    }

    @Override
    public boolean cancel(Long id, String name, boolean isReplication) {
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 获取到注册中心服务端地址
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return true;
        }
        serviceUrl.forEach((zone, url) -> {
            Assert.isTrue(StringUtils.isNotBlank(url), "serviceUrl can not be empty");
            Headers headers = getPairs(isReplication);
            String[] split = url.split(",");
            for (String s : split) {
                if (s.endsWith("/")) {
                    s = s + id + "/" + name;
                } else {
                    s = s + "/" + id + "/" + name;
                }
                String resp = OkHttpUtil.deleteSync(s, headers);
                log.info("服务下线结果:{}", resp);
            }
        });
        return true;
    }

    @Override
    public boolean renewal(Long id, String name, boolean isReplication) {
        log.info("服务续约:{},{},{}", id, name, isReplication);
        Assert.notNull(dp, "DiscoveryProperties can not be null");
        // 获取到注册中心服务端地址
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return true;
        }
        serviceUrl.forEach((zone, url) -> {
            Assert.isTrue(StringUtils.isNotBlank(url), "serviceUrl can not be empty");
            Headers headers = getPairs(isReplication);
            String[] split = url.split(",");
            for (String s : split) {
                if (s.endsWith("/")) {
                    s = s + id + "/" + name;
                } else {
                    s = s + "/" + id + "/" + name;
                }
                String resp = OkHttpUtil.putSync(s, headers);
                log.info("服务续租结果:{}", resp);
            }
        });
        return true;
    }

    @Override
    public void evit() {

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
