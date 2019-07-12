package com.setty.security.access.event;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/12 15:42
 */
public class AuthorizationFailureEvent extends AbstractAuthorizationEvent {

    private static final long serialVersionUID = -1620005012666289948L;

    private AccessDeniedException accessDeniedException;
    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param authentication that was found in the <code>SecurityContextHolder</code>
     * @param accessDeniedException that was returned by the
     * <code>AccessDecisionManager</code>
     *
     * @throws IllegalArgumentException if any null arguments are presented.
     */
    public AuthorizationFailureEvent(Object secureObject,
                                     Collection<ConfigAttribute> attributes, Authentication authentication,
                                     AccessDeniedException accessDeniedException) {
        super(secureObject);

        if ((attributes == null) || (authentication == null)
                || (accessDeniedException == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttributes = attributes;
        this.authentication = authentication;
        this.accessDeniedException = accessDeniedException;
    }

    // ~ Methods
    // ========================================================================================================

    public AccessDeniedException getAccessDeniedException() {
        return accessDeniedException;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }
}
