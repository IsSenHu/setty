package com.setty.security.core.authority.mapping;

import com.setty.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:37
 */
public interface GrantedAuthoritiesMapper {
    Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities);
}
