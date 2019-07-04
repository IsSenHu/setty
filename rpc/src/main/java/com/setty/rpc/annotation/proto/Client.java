package com.setty.rpc.annotation.proto;

import java.lang.annotation.*;

/**
 * proto client
 *
 * @author HuSen
 * create on 2019/7/3 18:26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Client {

    int moduleId() default 0;

    int methodId() default 0;

    Class requestType() default Void.class;
}
