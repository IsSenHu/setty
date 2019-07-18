package com.setty.security.authentication;

import com.setty.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:15
 */
public class RememberMeAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -3277859966101994038L;
    // ~ Instance fields
    // ================================================================================================

    private final Object principal;
    private final int keyHash;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructor.
     *
     * @param key         to identify if this object made by an authorised client
     * @param principal   the principal (typically a <code>UserDetails</code>)
     * @param authorities the authorities granted to the principal
     * @throws IllegalArgumentException if a <code>null</code> was passed
     */
    public RememberMeAuthenticationToken(String key, Object principal,
                                         Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        if ((key == null) || ("".equals(key)) || (principal == null)
                || "".equals(principal)) {
            throw new IllegalArgumentException(
                    "Cannot pass null or empty values to constructor");
        }

        this.keyHash = key.hashCode();
        this.principal = principal;
        setAuthenticated(true);
    }

    /**
     * Private Constructor to help in Jackson deserialization.
     *
     * @param keyHash     hashCode of above given key.
     * @param principal   the principal (typically a <code>UserDetails</code>)
     * @param authorities the authorities granted to the principal
     * @since 4.2
     */
    private RememberMeAuthenticationToken(Integer keyHash, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        this.keyHash = keyHash;
        this.principal = principal;
        setAuthenticated(true);
    }

    // ~ Methods
    // ========================================================================================================

    /**
     * Always returns an empty <code>String</code>
     *
     * @return an empty String
     */
    @Override
    public Object getCredentials() {
        return "";
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken test = (RememberMeAuthenticationToken) obj;

            if (this.getKeyHash() != test.getKeyHash()) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.keyHash;
        return result;
    }
}
