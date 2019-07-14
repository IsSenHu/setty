package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * Thrown if an authentication request could not be processed due to a system problem that
 * occurred internally. It differs from {@link AuthenticationServiceException} in that it
 * would not be thrown if an external system has an internal error or failure. This
 * ensures that we can handle errors that are within our control distinctly from errors of
 * other systems. The advantage to this distinction is that the untrusted external system
 * should not be able to fill up logs and cause excessive IO. However, an internal system
 * should report errors.
 *
 * @author HuSen
 * create on 2019/7/12 16:44
 */
public class InternalAuthenticationServiceException extends AuthenticationException {

    private static final long serialVersionUID = -8697604295366950602L;

    public InternalAuthenticationServiceException(String msg, Throwable t) {
        super(msg, t);
    }

    public InternalAuthenticationServiceException(String msg) {
        super(msg);
    }
}
