package com.setty.security.access.intercept;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;
import com.setty.security.core.GrantedAuthority;
import com.setty.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 18:50
 */
public class RunAsManagerImpl implements RunAsManager {
    // ~ Instance fields
    // ================================================================================================

    private String key;
    private String rolePrefix = "ROLE_";

    // ~ Methods
    // ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(
                key,
                "A Key is required and should match that configured for the RunAsImplAuthenticationProvider");
    }

    @Override
    public Authentication buildRunAs(Authentication authentication, Object object,
                                     Collection<ConfigAttribute> attributes) {
        List<GrantedAuthority> newAuthorities = new ArrayList<>();

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                GrantedAuthority extraAuthority = new SimpleGrantedAuthority(
                        getRolePrefix() + attribute.getAttribute());
                newAuthorities.add(extraAuthority);
            }
        }

        if (newAuthorities.size() == 0) {
            return null;
        }

        // Add existing authorities
        newAuthorities.addAll(authentication.getAuthorities());

        return new RunAsUserToken(this.key, authentication.getPrincipal(),
                authentication.getCredentials(), newAuthorities,
                authentication.getClass());
    }

    public String getKey() {
        return key;
    }

    public String getRolePrefix() {
        return rolePrefix;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Allows the default role prefix of <code>ROLE_</code> to be overridden. May be set
     * to an empty value, although this is usually not desirable.
     *
     * @param rolePrefix the new prefix
     */
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null
                && attribute.getAttribute().startsWith("RUN_AS_");
    }

    /**
     * This implementation supports any type of class, because it does not query the
     * presented secure object.
     *
     * @param clazz the secure object
     *
     * @return always <code>true</code>
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
