package com.setty.security.core.context;

import com.setty.security.core.Authentication;

import java.io.Serializable;

/**
 * 当前执行线程关联的最小安全信息的接口
 *
 * @author HuSen
 * create on 2019/7/12 13:32
 */
public interface SecurityContext extends Serializable {

    /**
     * Obtains the currently authenticated principal, or an authentication request token.
     *
     * @return the <code>Authentication</code> or <code>null</code> if no authentication
     * information is available
     */
    Authentication getAuthentication();

    /**
     * Changes the currently authenticated principal, or removes the authentication
     * information.
     *
     * @param authentication the new <code>Authentication</code> token, or
     * <code>null</code> if no further authentication information should be stored
     */
    void setAuthentication(Authentication authentication);
}
