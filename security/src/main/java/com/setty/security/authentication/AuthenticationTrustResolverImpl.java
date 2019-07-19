package com.setty.security.authentication;

import com.setty.security.core.Authentication;

/**
 * @author HuSen
 * create on 2019/7/18 18:13
 */
public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

    private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
    private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;

    // ~ Methods
    // ========================================================================================================

    Class<? extends Authentication> getAnonymousClass() {
        return anonymousClass;
    }

    Class<? extends Authentication> getRememberMeClass() {
        return rememberMeClass;
    }

    @Override
    public boolean isAnonymous(Authentication authentication) {
        if ((anonymousClass == null) || (authentication == null)) {
            return false;
        }

        return anonymousClass.isAssignableFrom(authentication.getClass());
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        if ((rememberMeClass == null) || (authentication == null)) {
            return false;
        }

        return rememberMeClass.isAssignableFrom(authentication.getClass());
    }

    public void setAnonymousClass(Class<? extends Authentication> anonymousClass) {
        this.anonymousClass = anonymousClass;
    }

    public void setRememberMeClass(Class<? extends Authentication> rememberMeClass) {
        this.rememberMeClass = rememberMeClass;
    }
}
