package com.setty.discovery.config;

import com.setty.discovery.properties.DiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author HuSen
 * create on 2019/7/5 18:33
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DiscoveryProperties.class)
public class EnableDiscoveryConfiguration {

    private final DiscoveryProperties dp;

    @Autowired
    public EnableDiscoveryConfiguration(DiscoveryProperties dp) {
        this.dp = dp;
    }
}
