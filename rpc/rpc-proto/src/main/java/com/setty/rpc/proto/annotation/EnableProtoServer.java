package com.setty.rpc.proto.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/3 15:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableProtoServer {
}
