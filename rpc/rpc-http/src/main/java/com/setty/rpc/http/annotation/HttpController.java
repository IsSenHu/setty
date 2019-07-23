package com.setty.rpc.http.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/3 15:09
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HttpController {
    String value();
}
