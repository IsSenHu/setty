package com.setty.security.access;

/**
 * Thrown if an Authentication object does not hold a required authority.
 *
 * @author HuSen
 * create on 2019/7/12 15:03
 */
public class AccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = -4705797313285847267L;

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public AccessDeniedException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public AccessDeniedException(String msg, Throwable t) {
        super(msg, t);
    }
}
