package com.setty.rpc.http.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/3 15:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableHttpServer {
}
