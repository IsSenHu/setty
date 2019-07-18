package com.setty.security.access.annotation;

import java.lang.annotation.*;

/**
 * @author HuSen
 * create on 2019/7/18 17:41
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Secured {

    /**
     * Returns the list of security configuration attributes (e.g.&nbsp;ROLE_USER, ROLE_ADMIN).
     *
     * @return String[] The secure method attributes
     */
    String[] value();
}
