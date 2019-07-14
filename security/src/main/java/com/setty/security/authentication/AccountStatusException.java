package com.setty.security.authentication;

import com.setty.security.core.AuthenticationException;

/**
 * Base class for authentication exceptions which are caused by a particular user account
 * status (locked, disabled etc).
 *
 * @author HuSen
 * create on 2019/7/12 16:43
 */
public class AccountStatusException extends AuthenticationException {

    private static final long serialVersionUID = 3426454547100259120L;

    public AccountStatusException(String msg, Throwable t) {
        super(msg, t);
    }

    public AccountStatusException(String msg) {
        super(msg);
    }
}
