package com.setty.security.core.authority;

import com.setty.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/7/12 16:40
 */
public class SimpleGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 6303742757543233966L;

    private final String role;

    public SimpleGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleGrantedAuthority) {
            return role.equals(((SimpleGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
