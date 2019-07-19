package com.setty.security.access.hierarchicalroles;

import com.setty.security.core.GrantedAuthority;
import com.setty.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:37
 */
public class RoleHierarchyAuthoritiesMapper implements GrantedAuthoritiesMapper {
    private final RoleHierarchy roleHierarchy;

    public RoleHierarchyAuthoritiesMapper(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return roleHierarchy.getReachableGrantedAuthorities(authorities);
    }
}
