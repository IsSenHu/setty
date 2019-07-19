package com.setty.security.access.method;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.access.SecurityMetadataSource;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 17:43
 */
public interface MethodSecurityMetadataSource extends SecurityMetadataSource {

    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass);
}
