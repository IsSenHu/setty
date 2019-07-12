package com.setty.security.authentication;

import com.setty.security.core.Authentication;
import com.setty.security.core.AuthenticationException;
import com.setty.security.core.CredentialsContainer;
import com.setty.security.core.SpringSecurityMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * AuthenticationManager 默认实现
 *
 * @author HuSen
 * create on 2019/7/12 16:24
 */
@Slf4j
public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {

    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
    private List<AuthenticationProvider> providers = Collections.emptyList();
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private AuthenticationManager parent;
    /**
     * 验证后擦除凭证
     */
    private boolean eraseCredentialsAfterAuthentication = true;

    public ProviderManager(List<AuthenticationProvider> providers) {
        this(providers, null);
    }

    private ProviderManager(List<AuthenticationProvider> providers,
                            AuthenticationManager parent) {
        Assert.notNull(providers, "providers list cannot be null");
        this.providers = providers;
        this.parent = parent;
        checkState();
    }

    @Override
    public void afterPropertiesSet() {
        checkState();
    }

    private void checkState() {
        if (parent == null && providers.isEmpty()) {
            throw new IllegalArgumentException(
                    "A parent AuthenticationManager or a list "
                            + "of AuthenticationProviders is required");
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        boolean debug = log.isDebugEnabled();

        for (AuthenticationProvider provider : getProviders()) {
            if (!provider.supports(toTest)) {
                continue;
            }

            if (debug) {
                log.debug("Authentication attempt using "
                        + provider.getClass().getName());
            }

            try {
                result = provider.authenticate(authentication);

                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            } catch (AccountStatusException | InternalAuthenticationServiceException e) {
                prepareException(e, authentication);
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw e;
            } catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result == null && parent != null) {
            // Allow the parent to try.
            try {
                result = parent.authenticate(authentication);
            } catch (ProviderNotFoundException e) {
                // ignore as we will throw below if no other exception occurred prior to
                // calling parent and the parent
                // may throw ProviderNotFound even though a provider in the child already
                // handled the request
            } catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result != null) {
            if (eraseCredentialsAfterAuthentication
                    && (result instanceof CredentialsContainer)) {
                // Authentication is complete. Remove credentials and other secret data
                // from authentication
                ((CredentialsContainer) result).eraseCredentials();
            }

            eventPublisher.publishAuthenticationSuccess(result);
            return result;
        }

        // Parent was null, or didn't authenticate (or throw an exception).

        if (lastException == null) {
            lastException = new ProviderNotFoundException(messages.getMessage(
                    "ProviderManager.providerNotFound",
                    new Object[] { toTest.getName() },
                    "No AuthenticationProvider found for {0}"));
        }

        prepareException(lastException, authentication);

        throw lastException;
    }

    private void prepareException(AuthenticationException ex, Authentication auth) {
        eventPublisher.publishAuthenticationFailure(ex, auth);
    }

    /**
     * Copies the authentication details from a source Authentication object to a
     * destination one, provided the latter does not already have one set.
     *
     * @param source source authentication
     * @param dest   the destination authentication object
     */
    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

    public List<AuthenticationProvider> getProviders() {
        return providers;
    }

    public void setAuthenticationEventPublisher(
            AuthenticationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    /**
     * If set to, a resulting {@code Authentication} which implements the
     * {@code CredentialsContainer} interface will have its
     * {@link CredentialsContainer#eraseCredentials() eraseCredentials} method called
     * before it is returned from the {@code authenticate()} method.
     *
     * @param eraseSecretData set to {@literal false} to retain the credentials data in
     * memory. Defaults to {@literal true}.
     */
    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
}
