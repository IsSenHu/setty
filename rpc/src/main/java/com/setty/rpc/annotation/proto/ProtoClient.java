package com.setty.rpc.annotation.proto;

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
public @interface ProtoClient {

}
