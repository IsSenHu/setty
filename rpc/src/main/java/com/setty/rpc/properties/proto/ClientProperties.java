package com.setty.rpc.properties.proto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/3 15:30
 */
@ConfigurationProperties(prefix = "setty.proto.client")
@Getter
@Setter
@Component
public class ClientProperties {

    private String test;

    private Map<String, ClientP> clients;
}
