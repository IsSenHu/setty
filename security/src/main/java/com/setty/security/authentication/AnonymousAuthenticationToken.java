package com.setty.security.authentication;

import com.setty.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:14
 */
public class AnonymousAuthenticationToken extends AbstractAuthenticationToken implements Serializable {
    private static final long serialVersionUID = -4456736878369140666L;
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
    public AnonymousAuthenticationToken(String key, Object principal,
                                        Collection<? extends GrantedAuthority> authorities) {
        this(extractKeyHash(key), principal, authorities);
    }

    /**
     * Constructor helps in Jackson Deserialization
     *
     * @param keyHash     hashCode of provided Key, constructed by above constructor
     * @param principal   the principal (typically a <code>UserDetails</code>)
     * @param authorities the authorities granted to the principal
     * @since 4.2
     */
    private AnonymousAuthenticationToken(Integer keyHash, Object principal,
                                         Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        if (principal == null || "".equals(principal)) {
            throw new IllegalArgumentException("principal cannot be null or empty");
        }
        Assert.notEmpty(authorities, "authorities cannot be null or empty");

        this.keyHash = keyHash;
        this.principal = principal;
        setAuthenticated(true);
    }

    // ~ Methods
    // ========================================================================================================

    private static Integer extractKeyHash(String key) {
        Assert.hasLength(key, "key cannot be empty or null");
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof AnonymousAuthenticationToken) {
            AnonymousAuthenticationToken test = (AnonymousAuthenticationToken) obj;

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
}
