package com.setty.security.access;

import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:46
 */
public interface AfterInvocationProvider {

    // ~ Methods
    // ========================================================================================================

    Object decide(Authentication authentication, Object object,
                  Collection<ConfigAttribute> attributes, Object returnedObject)
            throws AccessDeniedException;

    /**
     * Indicates whether this <code>AfterInvocationProvider</code> is able to participate
     * in a decision involving the passed <code>ConfigAttribute</code>.
     * <p>
     * This allows the <code>AbstractSecurityInterceptor</code> to check every
     * configuration attribute can be consumed by the configured
     * <code>AccessDecisionManager</code> and/or <code>RunAsManager</code> and/or
     * <code>AccessDecisionManager</code>.
     * </p>
     *
     * @param attribute a configuration attribute that has been configured against the
     * <code>AbstractSecurityInterceptor</code>
     *
     * @return true if this <code>AfterInvocationProvider</code> can support the passed
     * configuration attribute
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * Indicates whether the <code>AfterInvocationProvider</code> is able to provide
     * "after invocation" processing for the indicated secured object type.
     *
     * @param clazz the class of secure object that is being queried
     *
     * @return true if the implementation can process the indicated class
     */
    boolean supports(Class<?> clazz);
}
