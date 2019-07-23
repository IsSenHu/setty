package com.setty.rpc.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HuSen
 * create on 2019/7/3 15:28
 */
@ConfigurationProperties(prefix = "setty.proto.server")
@Getter
@Setter
public class ServerProperties {
    private int port;
    private long appId;
    private String instanceName;
}
