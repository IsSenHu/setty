package com.setty.security.access.intercept;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * Creates a new temporary {@link Authentication} object for the current secure object
 * invocation only.
 *
 * @author HuSen
 * create on 2019/7/12 15:15
 */
public interface RunAsManager {

    /**
     * Returns a replacement <code>Authentication</code> object for the current secure
     * object invocation, or <code>null</code> if replacement not required.
     *
     * @param authentication the caller invoking the secure object
     * @param object the secured object being called
     * @param attributes the configuration attributes associated with the secure object
     * being invoked
     *
     * @return a replacement object to be used for duration of the secure object
     * invocation, or <code>null</code> if the <code>Authentication</code> should be left
     * as is
     */
    Authentication buildRunAs(Authentication authentication, Object object,
                              Collection<ConfigAttribute> attributes);

    /**
     * Indicates whether this <code>RunAsManager</code> is able to process the passed
     * <code>ConfigAttribute</code>.
     * <p>
     * This allows the <code>AbstractSecurityInterceptor</code> to check every
     * configuration attribute can be consumed by the configured
     * <code>AccessDecisionManager</code> and/or <code>RunAsManager</code> and/or
     * <code>AfterInvocationManager</code>.
     * </p>
     *
     * @param attribute a configuration attribute that has been configured against the
     * <code>AbstractSecurityInterceptor</code>
     *
     * @return <code>true</code> if this <code>RunAsManager</code> can support the passed
     * configuration attribute
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * Indicates whether the <code>RunAsManager</code> implementation is able to provide
     * run-as replacement for the indicated secure object type.
     *
     * @param clazz the class that is being queried
     *
     * @return true if the implementation can process the indicated class
     */
    boolean supports(Class<?> clazz);
}
