package com.setty.security.core;

/**
 * Abstract superclass for all exceptions related to an {@link Authentication} object
 * being invalid for whatever reason.
 *
 * @author HuSen
 * create on 2019/7/12 15:04
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = -8276493508572592471L;

    /**
     * Constructs an {@code AuthenticationException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public AuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public AuthenticationException(String msg) {
        super(msg);
    }
}
