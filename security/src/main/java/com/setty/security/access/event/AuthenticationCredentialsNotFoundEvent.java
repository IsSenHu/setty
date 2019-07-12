package com.setty.security.access.event;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Collection;

/**
 * Indicates a secure object invocation failed because the <code>Authentication</code>
 * could not be obtained from the <code>SecurityContextHolder</code>.
 *
 * @author HuSen
 * create on 2019/7/12 15:37
 */
public class AuthenticationCredentialsNotFoundEvent extends AbstractAuthorizationEvent {

    private static final long serialVersionUID = -6228423556236120851L;

    private AuthenticationCredentialsNotFoundException credentialsNotFoundException;
    private Collection<ConfigAttribute> configAttribs;

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param credentialsNotFoundException exception returned to the caller (contains
     * reason)
     *
     */
    public AuthenticationCredentialsNotFoundEvent(Object secureObject,
                                                  Collection<ConfigAttribute> attributes,
                                                  AuthenticationCredentialsNotFoundException credentialsNotFoundException) {
        super(secureObject);

        if ((attributes == null) || (credentialsNotFoundException == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttribs = attributes;
        this.credentialsNotFoundException = credentialsNotFoundException;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttribs;
    }

    public AuthenticationCredentialsNotFoundException getCredentialsNotFoundException() {
        return credentialsNotFoundException;
    }
}
