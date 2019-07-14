package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * Thrown if an authentication request is rejected because there is no
 *
 * @author HuSen
 * create on 2019/7/12 15:37
 */
public class AuthenticationCredentialsNotFoundException extends AuthenticationException {

    private static final long serialVersionUID = 463165090704041633L;

    public AuthenticationCredentialsNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationCredentialsNotFoundException(String msg) {
        super(msg);
    }
}
