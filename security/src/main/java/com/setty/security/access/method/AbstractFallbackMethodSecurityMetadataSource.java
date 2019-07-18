package com.setty.security.access.method;

import com.setty.security.access.ConfigAttribute;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * @author HuSen
 * create on 2019/7/18 17:43
 */
public abstract class AbstractFallbackMethodSecurityMetadataSource extends AbstractMethodSecurityMetadataSource {

    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        // The method may be on an interface, but we need attributes from the target
        // class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        // First try is the method in the target class.
        Collection<ConfigAttribute> attr = findAttributes(specificMethod, targetClass);
        if (attr != null) {
            return attr;
        }

        // Second try is the config attribute on the target class.
        attr = findAttributes(specificMethod.getDeclaringClass());
        if (attr != null) {
            return attr;
        }

        if (specificMethod != method || targetClass == null) {
            // Fallback is to look at the original method.
            attr = findAttributes(method, method.getDeclaringClass());
            if (attr != null) {
                return attr;
            }
            // Last fallback is the class of the original method.
            return findAttributes(method.getDeclaringClass());
        }
        return Collections.emptyList();
    }

    /**
     * Obtains the security metadata applicable to the specified method invocation.
     *
     * <p>
     * Note that the {@link Method#getDeclaringClass()} may not equal the
     * <code>targetClass</code>. Both parameters are provided to assist subclasses which
     * may wish to provide advanced capabilities related to method metadata being
     * "registered" against a method even if the target class does not declare the method
     * (i.e. the subclass may only inherit the method).
     *
     * @param method the method for the current invocation (never <code>null</code>)
     * @param targetClass the target class for the invocation (may be <code>null</code>)
     * @return the security metadata (or null if no metadata applies)
     */
    protected abstract Collection<ConfigAttribute> findAttributes(Method method,
                                                                  Class<?> targetClass);

    /**
     * Obtains the security metadata registered against the specified class.
     *
     * <p>
     * Subclasses should only return metadata expressed at a class level. Subclasses
     * should NOT aggregate metadata for each method registered against a class, as the
     * abstract superclass will separate invoke {@link #findAttributes(Method, Class)} for
     * individual methods as appropriate.
     *
     * @param clazz the target class for the invocation (never <code>null</code>)
     * @return the security metadata (or null if no metadata applies)
     */
    protected abstract Collection<ConfigAttribute> findAttributes(Class<?> clazz);
}
