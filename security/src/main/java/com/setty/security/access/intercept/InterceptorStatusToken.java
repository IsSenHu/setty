package com.setty.security.access.intercept;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.context.SecurityContext;

import java.util.Collection;

/**
 * A return object received by {@link AbstractSecurityInterceptor} subclasses.
 *
 * @author HuSen
 * create on 2019/7/12 15:27
 */
public class InterceptorStatusToken {

    private SecurityContext securityContext;
    private Collection<ConfigAttribute> attr;
    private Object secureObject;
    private boolean contextHolderRefreshRequired;

    public InterceptorStatusToken(SecurityContext securityContext,
                                  boolean contextHolderRefreshRequired, Collection<ConfigAttribute> attributes,
                                  Object secureObject) {
        this.securityContext = securityContext;
        this.contextHolderRefreshRequired = contextHolderRefreshRequired;
        this.attr = attributes;
        this.secureObject = secureObject;
    }

    public Collection<ConfigAttribute> getAttributes() {
        return attr;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public Object getSecureObject() {
        return secureObject;
    }

    public boolean isContextHolderRefreshRequired() {
        return contextHolderRefreshRequired;
    }
}
