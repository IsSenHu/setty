package com.setty.discovery.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/8 18:02
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDiscoveryClient {
}
