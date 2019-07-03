package com.setty.rpc.annotation.proto;

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
public @interface ProtoController {

    int moduleId() default 0;
}
