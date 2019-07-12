package com.setty.security.core.authority;

import com.setty.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author HuSen
 * create on 2019/7/12 16:38
 */
public abstract class AuthorityUtils {

    public static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

    /**
     * Creates a array of GrantedAuthority objects from a comma-separated string
     * representation (e.g. "ROLE_A, ROLE_B, ROLE_C").
     *
     * @param authorityString the comma-separated string
     * @return the authorities created by tokenizing the string
     */
    public static List<GrantedAuthority> commaSeparatedStringToAuthorityList(
            String authorityString) {
        return createAuthorityList(StringUtils
                .tokenizeToStringArray(authorityString, ","));
    }

    /**
     * Converts an array of GrantedAuthority objects to a Set.
     * @return a Set of the Strings obtained from each call to
     * GrantedAuthority.getAuthority()
     */
    public static Set<String> authorityListToSet(
            Collection<? extends GrantedAuthority> userAuthorities) {
        Set<String> set = new HashSet<>(userAuthorities.size());

        for (GrantedAuthority authority : userAuthorities) {
            set.add(authority.getAuthority());
        }

        return set;
    }

    public static List<GrantedAuthority> createAuthorityList(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }
}
