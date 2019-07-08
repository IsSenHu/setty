package com.setty.discovery.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 18:30
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "setty.discovery")
public class DiscoveryProperties {

    /**
     * 区 => 服务地址
     * */
    private Map<String, String> serviceUrl;

    private Integer leaseDuration = 90;

    private Integer renewalIntervalInSecs = 30;
}
