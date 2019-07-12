package com.setty.security.access.event;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/12 15:45
 */
public class AuthorizedEvent extends AbstractAuthorizationEvent {

    private static final long serialVersionUID = 1682073402259340502L;

    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param authentication that successfully called the secure object
     *
     */
    public AuthorizedEvent(Object secureObject, Collection<ConfigAttribute> attributes,
                           Authentication authentication) {
        super(secureObject);

        if ((attributes == null) || (authentication == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttributes = attributes;
        this.authentication = authentication;
    }

    // ~ Methods
    // ========================================================================================================

    public Authentication getAuthentication() {
        return authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }
}
