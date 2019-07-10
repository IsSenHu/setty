package com.setty.discovery.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author HuSen
 * create on 2019/7/5 18:30
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "setty.discovery")
public class DiscoveryProperties {

    public static final String DEFAULT_REGION = "siChuan";
    public static final String DEFAULT_ZONE = "hs";

    /**
     * zone => 服务地址
     * */
    private Map<String, String> serviceUrl;

    private Integer leaseDuration = 90;

    private Integer renewalIntervalInSecs = 30;

    private Long appId;

    private String host;

    private Integer port;

    private String instanceName = UUID.randomUUID().toString();

    private Boolean isRegistry = false;

    /**
     * 设置这个应用所在的区
     * */
    private String region = DEFAULT_REGION;

    /**
     * 区 => zones
     * availabilityZones.get(region)
     * 优先访问同自己一个Zone中的实例，其次才访问其他Zone中的实例。
     * 通过Region和Zone的两层级别定义，配合实际部署的物理结构，我们就可以有效的设计出区域性故障的容错集群。
     * */
    private Map<String, String> availabilityZones;

    /**
     * 这个应用的zone
     * */
    private String zone = DEFAULT_ZONE;

    /**
     * 先不实现该功能
     * 自我保护模式 通过当前注册的实例数，去计算每分钟应该从应用实例接收到的心跳数
     * 如果最近一分钟接收到的续约次数小于等于指定的阀值的话，则关闭租约失效剔除，禁止定时任务剔除失效的实例，从而保护注册信息
     * */
    private Boolean selfPreservation = true;
}
