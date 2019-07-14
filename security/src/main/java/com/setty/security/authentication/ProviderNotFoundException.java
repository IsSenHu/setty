package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * @author HuSen
 * create on 2019/7/12 16:55
 */
public class ProviderNotFoundException extends AuthenticationException {

    private static final long serialVersionUID = -4438485638076537756L;

    public ProviderNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public ProviderNotFoundException(String msg) {
        super(msg);
    }
}
