package com.setty.discovery.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 18:30
 */
@Component
@ConfigurationProperties(prefix = "setty.discovery")
public class DiscoveryProperties {
    private Map<String, String> serviceUrl;
}
