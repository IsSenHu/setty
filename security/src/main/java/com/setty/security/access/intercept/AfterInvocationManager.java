package com.setty.security.access.intercept;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/12 15:08
 */
public interface AfterInvocationManager {

    /**
     * Given the details of a secure object invocation including its returned
     * <code>Object</code>, make an access control decision or optionally modify the
     * returned <code>Object</code>.
     *
     * @param authentication the caller that invoked the method
     * @param object the secured object that was called
     * @param attributes the configuration attributes associated with the secured object
     * that was invoked
     * @param returnedObject the <code>Object</code> that was returned from the secure
     * object invocation
     *
     * @return the <code>Object</code> that will ultimately be returned to the caller (if
     * an implementation does not wish to modify the object to be returned to the caller,
     * the implementation should simply return the same object it was passed by the
     * <code>returnedObject</code> method argument)
     *
     * @throws AccessDeniedException if access is denied
     */
    Object decide(Authentication authentication, Object object,
                  Collection<ConfigAttribute> attributes, Object returnedObject)
            throws AccessDeniedException;

    /**
     * Indicates whether this <code>AfterInvocationManager</code> is able to process
     * "after invocation" requests presented with the passed <code>ConfigAttribute</code>.
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
     * @return true if this <code>AfterInvocationManager</code> can support the passed
     * configuration attribute
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * Indicates whether the <code>AfterInvocationManager</code> implementation is able to
     * provide access control decisions for the indicated secured object type.
     *
     * @param clazz the class that is being queried
     *
     * @return <code>true</code> if the implementation can process the indicated class
     */
    boolean supports(Class<?> clazz);
}
