package com.setty.security.core.context;

import com.setty.security.core.Authentication;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author HuSen
 * create on 2019/7/12 14:20
 */
@NoArgsConstructor
@AllArgsConstructor
public class SecurityContextImpl implements SecurityContext {

    private static final long serialVersionUID = -2943605118190808535L;

    private Authentication authentication;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecurityContextImpl) {
            SecurityContextImpl test = (SecurityContextImpl) obj;

            if ((this.getAuthentication() == null) && (test.getAuthentication() == null)) {
                return true;
            }

            return (this.getAuthentication() != null) && (test.getAuthentication() != null)
                    && this.getAuthentication().equals(test.getAuthentication());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.authentication == null) {
            return -1;
        }
        else {
            return this.authentication.hashCode();
        }
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());

        if (this.authentication == null) {
            sb.append(": Null authentication");
        }
        else {
            sb.append(": Authentication: ").append(this.authentication);
        }

        return sb.toString();
    }
}
