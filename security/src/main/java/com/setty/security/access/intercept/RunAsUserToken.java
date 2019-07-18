package com.setty.security.access.intercept;

import com.setty.security.authentication.AbstractAuthenticationToken;
import com.setty.security.core.Authentication;
import com.setty.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:49
 */
public class RunAsUserToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -6578056683714771735L;

// ~ Instance fields
    // ================================================================================================

    private final Class<? extends Authentication> originalAuthentication;
    private final Object credentials;
    private final Object principal;
    private final int keyHash;

    // ~ Constructors
    // ===================================================================================================

    public RunAsUserToken(String key, Object principal, Object credentials,
                          Collection<? extends GrantedAuthority> authorities,
                          Class<? extends Authentication> originalAuthentication) {
        super(authorities);
        this.keyHash = key.hashCode();
        this.principal = principal;
        this.credentials = credentials;
        this.originalAuthentication = originalAuthentication;
        setAuthenticated(true);
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    public Class<? extends Authentication> getOriginalAuthentication() {
        return this.originalAuthentication;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        String className = this.originalAuthentication == null ? null
                : this.originalAuthentication.getName();
        sb.append("; Original Class: ").append(className);

        return sb.toString();
    }
}
