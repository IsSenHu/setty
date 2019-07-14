package com.setty.security.core;

/**
 * @author HuSen
 * create on 2019/7/12 16:42
 */
public interface AuthenticatedPrincipal {

    /**
     * Returns the name of the authenticated <code>Principal</code>. Never <code>null</code>.
     *
     * @return the name of the authenticated <code>Principal</code>
     */
    String getName();
}
