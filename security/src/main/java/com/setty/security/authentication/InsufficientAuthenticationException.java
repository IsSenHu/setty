package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * Thrown if an authentication request is rejected because the credentials are not
 * sufficiently trusted.
 *
 * @author HuSen
 * create on 2019/7/12 15:05
 */
public class InsufficientAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 9020211123537452255L;

    public InsufficientAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public InsufficientAuthenticationException(String msg) {
        super(msg);
    }
}
