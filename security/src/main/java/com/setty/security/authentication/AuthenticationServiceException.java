package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * @author HuSen
 * create on 2019/7/12 15:14
 */
public class AuthenticationServiceException extends AuthenticationException {

    private static final long serialVersionUID = 4007746425870269071L;

    public AuthenticationServiceException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationServiceException(String msg) {
        super(msg);
    }
}
