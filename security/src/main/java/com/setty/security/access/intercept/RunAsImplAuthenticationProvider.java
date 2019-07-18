package com.setty.security.access.intercept;

import com.setty.security.authentication.AuthenticationProvider;
import com.setty.security.authentication.BadCredentialsException;
import com.setty.security.core.Authentication;
import com.setty.security.core.AuthenticationException;
import com.setty.security.core.SpringSecurityMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/7/18 18:48
 */
public class RunAsImplAuthenticationProvider implements InitializingBean, AuthenticationProvider, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String key;

    // ~ Methods
    // ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(key,
                "A Key is required and should match that configured for the RunAsManagerImpl");
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        RunAsUserToken token = (RunAsUserToken) authentication;

        if (token.getKeyHash() == key.hashCode()) {
            return authentication;
        }
        else {
            throw new BadCredentialsException(messages.getMessage(
                    "RunAsImplAuthenticationProvider.incorrectKey",
                    "The presented RunAsUserToken does not contain the expected key"));
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public boolean supports(Class<?> authentication) {
        return RunAsUserToken.class.isAssignableFrom(authentication);
    }
}
