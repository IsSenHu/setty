package com.setty.security.access.intercept;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/12 15:16
 */
final class NullRunAsManager implements RunAsManager {

    @Override
    public Authentication buildRunAs(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        return null;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
