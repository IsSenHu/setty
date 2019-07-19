package com.setty.security.core.parameters;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/18 18:17
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface P {
    /**
     * The parameter name
     * @return
     */
    String value();
}
