package com.setty.rpc.properties.proto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HuSen
 * create on 2019/7/3 15:30
 */
@ConfigurationProperties(prefix = "setty.proto")
@Getter
@Setter
public class ProtoProperties {
}
