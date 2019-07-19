package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * @author HuSen
 * create on 2019/7/18 18:50
 */
public class BadCredentialsException extends AuthenticationException {
    private static final long serialVersionUID = 4239415251935866900L;
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>BadCredentialsException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public BadCredentialsException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>BadCredentialsException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public BadCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }
}
