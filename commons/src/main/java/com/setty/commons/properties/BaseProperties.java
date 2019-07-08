package com.setty.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HuSen
 * create on 2019/7/8
 */
@Data
@Component
@ConfigurationProperties(prefix = "setty.base")
public class BaseProperties {
    private int appCode;
}
