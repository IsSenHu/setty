package com.setty.security.authentication;

import com.setty.security.core.Authentication;
import com.setty.security.core.AuthenticationException;

/**
 * @author HuSen
 * create on 2019/7/12 16:26
 */
public interface AuthenticationEventPublisher {

    void publishAuthenticationSuccess(Authentication authentication);

    void publishAuthenticationFailure(AuthenticationException exception,
                                      Authentication authentication);
}
