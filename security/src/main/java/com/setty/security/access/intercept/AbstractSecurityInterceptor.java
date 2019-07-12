package com.setty.security.access.intercept;

import com.setty.security.access.AccessDecisionManager;
import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.ConfigAttribute;
import com.setty.security.access.SecurityMetadataSource;
import com.setty.security.access.event.AuthenticationCredentialsNotFoundEvent;
import com.setty.security.access.event.AuthorizationFailureEvent;
import com.setty.security.access.event.AuthorizedEvent;
import com.setty.security.access.event.PublicInvocationEvent;
import com.setty.security.authentication.AuthenticationCredentialsNotFoundException;
import com.setty.security.authentication.AuthenticationManager;
import com.setty.security.authentication.AuthenticationServiceException;
import com.setty.security.core.Authentication;
import com.setty.security.core.AuthenticationException;
import com.setty.security.core.SpringSecurityMessageSource;
import com.setty.security.core.context.SecurityContext;
import com.setty.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author HuSen
 * create on 2019/7/12 14:56
 */
@Slf4j
public abstract class AbstractSecurityInterceptor implements InitializingBean, ApplicationEventPublisherAware, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private ApplicationEventPublisher eventPublisher;
    private AccessDecisionManager accessDecisionManager;
    private AfterInvocationManager afterInvocationManager;
    private AuthenticationManager authenticationManager = new NoOpAuthenticationManager();
    private RunAsManager runAsManager = new NullRunAsManager();

    private boolean alwaysReauthenticate = false;
    private boolean rejectPublicInvocations = false;
    private boolean validateConfigAttributes = true;
    private boolean publishAuthorizationSuccess = false;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getSecureObjectClass(),
                "Subclass must provide a non-null response to getSecureObjectClass()");
        Assert.notNull(this.messages, "A message source must be set");
        Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
        Assert.notNull(this.accessDecisionManager, "An AccessDecisionManager is required");
        Assert.notNull(this.runAsManager, "A RunAsManager is required");

        Assert.notNull(this.obtainSecurityMetadataSource(),
                "An SecurityMetadataSource is required");
        Assert.isTrue(this.obtainSecurityMetadataSource()
                        .supports(getSecureObjectClass()),
                () -> "SecurityMetadataSource does not support secure object class: "
                        + getSecureObjectClass());

        Assert.isTrue(this.runAsManager.supports(getSecureObjectClass()),
                () -> "RunAsManager does not support secure object class: "
                        + getSecureObjectClass());

        Assert.isTrue(this.accessDecisionManager.supports(getSecureObjectClass()),
                () -> "AccessDecisionManager does not support secure object class: "
                        + getSecureObjectClass());

        if (this.afterInvocationManager != null) {
            Assert.isTrue(this.afterInvocationManager.supports(getSecureObjectClass()),
                    () -> "AfterInvocationManager does not support secure object class: "
                            + getSecureObjectClass());
        }

        if (this.validateConfigAttributes) {
            Collection<ConfigAttribute> attributeDefs = this
                    .obtainSecurityMetadataSource().getAllConfigAttributes();

            if (attributeDefs == null) {
                log.warn("Could not validate configuration attributes as the SecurityMetadataSource did not return "
                        + "any attributes from getAllConfigAttributes()");
                return;
            }

            Set<ConfigAttribute> unsupportedAttrs = new HashSet<>();

            for (ConfigAttribute attr : attributeDefs) {
                boolean unSupport = !this.runAsManager.supports(attr)
                        && !this.accessDecisionManager.supports(attr)
                        && ((this.afterInvocationManager == null) || !this.afterInvocationManager
                        .supports(attr));
                if (unSupport) {
                    unsupportedAttrs.add(attr);
                }
            }

            if (unsupportedAttrs.size() != 0) {
                throw new IllegalArgumentException(
                        "Unsupported configuration attributes: " + unsupportedAttrs);
            }

            log.debug("Validated configuration attributes");
        }
    }

    protected InterceptorStatusToken beforeInvocation(Object object) {
        Assert.notNull(object, "Object was null");
        final boolean debug = log.isDebugEnabled();

        if (!getSecureObjectClass().isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(
                    "Security invocation attempted for object "
                            + object.getClass().getName()
                            + " but AbstractSecurityInterceptor only configured to support secure objects of type: "
                            + getSecureObjectClass());
        }

        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource()
                .getAttributes(object);

        if (attributes == null || attributes.isEmpty()) {
            if (rejectPublicInvocations) {
                throw new IllegalArgumentException(
                        "Secure object invocation "
                                + object
                                + " was denied as public invocations are not allowed via this interceptor. "
                                + "This indicates a configuration error because the "
                                + "rejectPublicInvocations property is set to 'true'");
            }

            if (debug) {
                log.debug("Public object - authentication not attempted");
            }

            publishEvent(new PublicInvocationEvent(object));

            return null;
        }

        if (debug) {
            log.debug("Secure object: " + object + "; Attributes: " + attributes);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            credentialsNotFound(messages.getMessage(
                    "AbstractSecurityInterceptor.authenticationNotFound",
                    "An Authentication object was not found in the SecurityContext"),
                    object, attributes);
        }

        Authentication authenticated = authenticateIfRequired();

        // 尝试授权
        try {
            this.accessDecisionManager.decide(authenticated, object, attributes);
        } catch (AccessDeniedException accessDeniedException) {
            publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated,
                    accessDeniedException));

            throw accessDeniedException;
        }

        if (debug) {
            log.debug("Authorization successful");
        }

        if (publishAuthorizationSuccess) {
            publishEvent(new AuthorizedEvent(object, attributes, authenticated));
        }

        // 尝试以其他身份运行
        Authentication runAs = this.runAsManager.buildRunAs(authenticated, object,
                attributes);

        if (runAs == null) {
            if (debug) {
                log.debug("RunAsManager did not change Authentication object");
            }

            // no further work post-invocation
            return new InterceptorStatusToken(SecurityContextHolder.getContext(), false, attributes, object);
        } else {
            if (debug) {
                log.debug("Switching to RunAs Authentication: " + runAs);
            }

            SecurityContext origCtx = SecurityContextHolder.getContext();
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
            SecurityContextHolder.getContext().setAuthentication(runAs);

            // need to revert to token.Authenticated post-invocation
            return new InterceptorStatusToken(origCtx, true, attributes, object);
        }
    }

    /**
     * Cleans up the work of the <tt>AbstractSecurityInterceptor</tt> after the secure
     * object invocation has been completed. This method should be invoked after the
     * secure object invocation and before afterInvocation regardless of the secure object
     * invocation returning successfully (i.e. it should be done in a finally block).
     *
     * @param token as returned by the {@link #beforeInvocation(Object)} method
     */
    protected void finallyInvocation(InterceptorStatusToken token) {
        if (token != null && token.isContextHolderRefreshRequired()) {
            if (log.isDebugEnabled()) {
                log.debug("Reverting to original Authentication: "
                        + token.getSecurityContext().getAuthentication());
            }

            SecurityContextHolder.setContext(token.getSecurityContext());
        }
    }

    /**
     * Completes the work of the <tt>AbstractSecurityInterceptor</tt> after the secure
     * object invocation has been completed.
     *
     * @param token          as returned by the {@link #beforeInvocation(Object)} method
     * @param returnedObject any object returned from the secure object invocation (may be
     *                       <tt>null</tt>)
     * @return the object the secure object invocation should ultimately return to its
     * caller (may be <tt>null</tt>)
     */
    protected Object afterInvocation(InterceptorStatusToken token, Object returnedObject) {
        if (token == null) {
            // public object
            return returnedObject;
        }

        // continue to clean in this method for passivity
        finallyInvocation(token);

        if (afterInvocationManager != null) {
            // Attempt after invocation handling
            try {
                returnedObject = afterInvocationManager.decide(token.getSecurityContext()
                        .getAuthentication(), token.getSecureObject(), token
                        .getAttributes(), returnedObject);
            } catch (AccessDeniedException accessDeniedException) {
                AuthorizationFailureEvent event = new AuthorizationFailureEvent(
                        token.getSecureObject(), token.getAttributes(), token
                        .getSecurityContext().getAuthentication(),
                        accessDeniedException);
                publishEvent(event);

                throw accessDeniedException;
            }
        }

        return returnedObject;
    }

    /**
     * Checks the current authentication token and passes it to the AuthenticationManager
     * if {@link Authentication#isAuthenticated()}
     * returns false or the property <tt>alwaysReauthenticate</tt> has been set to true.
     *
     * @return an authenticated <tt>Authentication</tt> object.
     */
    private Authentication authenticateIfRequired() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication.isAuthenticated() && !alwaysReauthenticate) {
            if (log.isDebugEnabled()) {
                log.debug("Previously Authenticated: " + authentication);
            }

            return authentication;
        }

        authentication = authenticationManager.authenticate(authentication);

        // We don't authenticated.setAuthentication(true), because each provider should do
        // that
        if (log.isDebugEnabled()) {
            log.debug("Successfully Authenticated: " + authentication);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    /**
     * Helper method which generates an exception containing the passed reason, and
     * publishes an event to the application context.
     * <p>
     * Always throws an exception.
     *
     * @param reason        to be provided in the exception detail
     * @param secureObject  that was being called
     * @param configAttribs that were defined for the secureObject
     */
    private void credentialsNotFound(String reason, Object secureObject,
                                     Collection<ConfigAttribute> configAttribs) {
        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException(
                reason);

        AuthenticationCredentialsNotFoundEvent event = new AuthenticationCredentialsNotFoundEvent(
                secureObject, configAttribs, exception);
        publishEvent(event);

        throw exception;
    }

    public AccessDecisionManager getAccessDecisionManager() {
        return accessDecisionManager;
    }

    public AfterInvocationManager getAfterInvocationManager() {
        return afterInvocationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public RunAsManager getRunAsManager() {
        return runAsManager;
    }

    /**
     * Indicates the type of secure objects the subclass will be presenting to the
     * abstract parent for processing. This is used to ensure collaborators wired to the
     * {@code AbstractSecurityInterceptor} all support the indicated secure object class.
     *
     * @return the type of secure object the subclass provides services for
     */
    public abstract Class<?> getSecureObjectClass();

    public boolean isAlwaysReauthenticate() {
        return alwaysReauthenticate;
    }

    public boolean isRejectPublicInvocations() {
        return rejectPublicInvocations;
    }

    public boolean isValidateConfigAttributes() {
        return validateConfigAttributes;
    }

    public abstract SecurityMetadataSource obtainSecurityMetadataSource();

    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

    public void setAfterInvocationManager(AfterInvocationManager afterInvocationManager) {
        this.afterInvocationManager = afterInvocationManager;
    }

    /**
     * Indicates whether the <code>AbstractSecurityInterceptor</code> should ignore the
     * {@link Authentication#isAuthenticated()} property. Defaults to <code>false</code>,
     * meaning by default the <code>Authentication.isAuthenticated()</code> property is
     * trusted and re-authentication will not occur if the principal has already been
     * authenticated.
     *
     * @param alwaysReauthenticate <code>true</code> to force
     *                             <code>AbstractSecurityInterceptor</code> to disregard the value of
     *                             <code>Authentication.isAuthenticated()</code> and always re-authenticate the
     *                             request (defaults to <code>false</code>).
     */
    public void setAlwaysReauthenticate(boolean alwaysReauthenticate) {
        this.alwaysReauthenticate = alwaysReauthenticate;
    }

    @Override
    public void setApplicationEventPublisher(
            @NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public void setAuthenticationManager(AuthenticationManager newManager) {
        this.authenticationManager = newManager;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * Only {@code AuthorizationFailureEvent} will be published. If you set this property
     * to {@code true}, {@code AuthorizedEvent}s will also be published.
     *
     * @param publishAuthorizationSuccess default value is {@code false}
     */
    public void setPublishAuthorizationSuccess(boolean publishAuthorizationSuccess) {
        this.publishAuthorizationSuccess = publishAuthorizationSuccess;
    }

    /**
     * By rejecting public invocations (and setting this property to <tt>true</tt>),
     * essentially you are ensuring that every secure object invocation advised by
     * <code>AbstractSecurityInterceptor</code> has a configuration attribute defined.
     * This is useful to ensure a "fail safe" mode where undeclared secure objects will be
     * rejected and configuration omissions detected early. An
     * <tt>IllegalArgumentException</tt> will be thrown by the
     * <tt>AbstractSecurityInterceptor</tt> if you set this property to <tt>true</tt> and
     * an attempt is made to invoke a secure object that has no configuration attributes.
     *
     * @param rejectPublicInvocations set to <code>true</code> to reject invocations of
     * secure objects that have no configuration attributes (by default it is
     * <code>false</code> which treats undeclared secure objects as "public" or
     * unauthorized).
     */
    public void setRejectPublicInvocations(boolean rejectPublicInvocations) {
        this.rejectPublicInvocations = rejectPublicInvocations;
    }

    public void setRunAsManager(RunAsManager runAsManager) {
        this.runAsManager = runAsManager;
    }

    public void setValidateConfigAttributes(boolean validateConfigAttributes) {
        this.validateConfigAttributes = validateConfigAttributes;
    }

    private void publishEvent(ApplicationEvent event) {
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(event);
        }
    }

    private static class NoOpAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new AuthenticationServiceException("Cannot authenticate "
                    + authentication);
        }
    }
}
