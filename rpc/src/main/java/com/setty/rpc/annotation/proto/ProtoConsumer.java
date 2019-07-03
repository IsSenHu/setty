package com.setty.rpc.annotation.proto;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/3 15:16
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtoConsumer {
    int value() default 0;
}
