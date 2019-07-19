package com.setty.security.access.method;

import com.setty.security.access.ConfigAttribute;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 17:43
 */
@Slf4j
public abstract class AbstractMethodSecurityMetadataSource implements MethodSecurityMetadataSource {

    @Override
    public final Collection<ConfigAttribute> getAttributes(Object object) {
        if (object instanceof MethodInvocation) {
            MethodInvocation mi = (MethodInvocation) object;
            Object target = mi.getThis();
            Class<?> targetClass = null;

            if (target != null) {
                targetClass = target instanceof Class<?> ? (Class<?>) target
                        : AopProxyUtils.ultimateTargetClass(target);
            }
            Collection<ConfigAttribute> attrs = getAttributes(mi.getMethod(), targetClass);
            if (attrs != null && !attrs.isEmpty()) {
                return attrs;
            }
            if (target != null && !(target instanceof Class<?>)) {
                attrs = getAttributes(mi.getMethod(), target.getClass());
            }
            return attrs;
        }

        throw new IllegalArgumentException("Object must be a non-null MethodInvocation");
    }

    @Override
    public final boolean supports(Class<?> clazz) {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }
}
