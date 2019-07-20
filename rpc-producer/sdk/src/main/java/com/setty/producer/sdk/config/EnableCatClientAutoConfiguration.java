package com.setty.producer.sdk.config;

import com.setty.producer.sdk.annotation.EnableCatClient;
import com.setty.rpc.proto.annotation.EnableProtoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/**
 * @author HuSen
 * create on 2019/7/5 11:19
 */
@ConditionalOnBean(annotation = EnableCatClient.class)
@EnableProtoClient(packages = "com.setty.producer.sdk.client")
public class EnableCatClientAutoConfiguration {
}
