package com.setty.rpc.core.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HuSen
 * create on 2019/7/4 17:45
 */
@Getter
@Setter
public class ClientP {
    private Long appId;
    private String host;
    private Integer port;
    private Integer connectionTimeout;
}
