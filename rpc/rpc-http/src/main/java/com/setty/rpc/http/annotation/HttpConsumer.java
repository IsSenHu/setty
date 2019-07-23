package com.setty.rpc.http.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/3 15:16
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpConsumer {
    String value();
}
