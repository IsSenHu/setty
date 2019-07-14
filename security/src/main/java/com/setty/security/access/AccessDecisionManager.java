package com.setty.security.access;

import com.setty.security.authentication.InsufficientAuthenticationException;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * Makes a final access control (authorization) decision.
 *
 * @author HuSen
 * create on 2019/7/12 15:01
 */
public interface AccessDecisionManager {

    /**
     * Resolves an access control decision for the passed parameters.
     *
     * @param authentication the caller invoking the method (not null)
     * @param object the secured object being called
     * @param configAttributes the configuration attributes associated with the secured
     * object being invoked
     *
     * @throws AccessDeniedException if access is denied as the authentication does not
     * hold a required authority or ACL privilege
     * @throws InsufficientAuthenticationException if access is denied as the
     * authentication does not provide a sufficient level of trust
     */
    void decide(Authentication authentication, Object object,
                Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
            InsufficientAuthenticationException;

    /**
     * Indicates whether this <code>AccessDecisionManager</code> is able to process
     * authorization requests presented with the passed <code>ConfigAttribute</code>.
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
     * @return true if this <code>AccessDecisionManager</code> can support the passed
     * configuration attribute
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * Indicates whether the <code>AccessDecisionManager</code> implementation is able to
     * provide access control decisions for the indicated secured object type.
     *
     * @param clazz the class that is being queried
     *
     * @return <code>true</code> if the implementation can process the indicated class
     */
    boolean supports(Class<?> clazz);
}
