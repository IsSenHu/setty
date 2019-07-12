package com.setty.security.core;

import java.io.Serializable;

/**
 * 在应用程序范围你，赋予主体的权限
 *
 * @author HuSen
 * create on 2019/7/12 13:55
 */
public interface GrantedAuthority extends Serializable {

    /**
     * If the GrantedAuthority can be represented as a String
     * and that String is sufficient in precision to be relied
     * upon for an access control decision by an AccessDecisionManager
     * (or delegate), this method should return such a String.
     *
     * If the GrantedAuthority cannot be expressed with sufficient
     * precision as a String, null should be returned. Returning null
     * will require an AccessDecisionManager (or delegate) to specifically
     * support the GrantedAuthority implementation, so returning null
     * should be avoided unless actually required.
     *
     * @return a representation of the granted authority (or null if the granted authority cannot be expressed as a String with sufficient precision)
     */
    String getAuthority();
}
