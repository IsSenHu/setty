package com.setty.security.core;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author HuSen
 * create on 2019/7/12 14:58
 */
public class SpringSecurityMessageSource extends ResourceBundleMessageSource {

    private SpringSecurityMessageSource() {
        setBasename("org.springframework.security.messages");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new SpringSecurityMessageSource());
    }
}
