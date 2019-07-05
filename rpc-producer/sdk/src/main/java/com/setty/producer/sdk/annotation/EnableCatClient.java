package com.setty.producer.sdk.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/5 11:19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCatClient {
}
