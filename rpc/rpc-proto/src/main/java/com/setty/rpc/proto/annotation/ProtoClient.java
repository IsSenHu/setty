package com.setty.rpc.proto.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * proto 通信调用客户端
 *
 * @author HuSen
 * create on 2019/7/3 18:26
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ProtoClient {
    long appId() default 0;
}
