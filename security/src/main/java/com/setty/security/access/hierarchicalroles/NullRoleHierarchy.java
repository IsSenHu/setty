package com.setty.security.access.hierarchicalroles;

import com.setty.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:36
 */
public final class NullRoleHierarchy implements RoleHierarchy {

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return authorities;
    }
}
