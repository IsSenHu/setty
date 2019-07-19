package com.setty.security.access.expression.method;

import com.setty.security.access.PermissionCacheOptimizer;
import com.setty.security.access.expression.AbstractSecurityExpressionHandler;
import com.setty.security.access.expression.ExpressionUtils;
import com.setty.security.authentication.AuthenticationTrustResolver;
import com.setty.security.authentication.AuthenticationTrustResolverImpl;
import com.setty.security.core.Authentication;
import com.setty.security.core.parameters.DefaultSecurityParameterNameDiscoverer;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 18:11
 */
@Slf4j
public class DefaultMethodSecurityExpressionHandler extends AbstractSecurityExpressionHandler<MethodInvocation> implements MethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultSecurityParameterNameDiscoverer();
    private PermissionCacheOptimizer permissionCacheOptimizer = null;
    private String defaultRolePrefix = "ROLE_";

    public DefaultMethodSecurityExpressionHandler() {
    }

    /**
     * Uses a {@link MethodSecurityEvaluationContext} as the <tt>EvaluationContext</tt>
     * implementation.
     */
    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication auth,
                                                                     MethodInvocation mi) {
        return new MethodSecurityEvaluationContext(auth, mi, getParameterNameDiscoverer());
    }

    /**
     * Creates the root object for expression evaluation.
     */
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(
                authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix(getDefaultRolePrefix());

        return root;
    }

    /**
     * Filters the {@code filterTarget} object (which must be either a collection or an
     * array), by evaluating the supplied expression.
     * <p>
     * If a {@code Collection} is used, the original instance will be modified to contain
     * the elements for which the permission expression evaluates to {@code true}. For an
     * array, a new array instance will be returned.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object filter(Object filterTarget, Expression filterExpression,
                         EvaluationContext ctx) {
        MethodSecurityExpressionOperations rootObject = (MethodSecurityExpressionOperations) ctx
                .getRootObject().getValue();
        final boolean debug = log.isDebugEnabled();
        List retainList;

        if (debug) {
            log.debug("Filtering with expression: "
                    + filterExpression.getExpressionString());
        }

        if (filterTarget instanceof Collection) {
            Collection collection = (Collection) filterTarget;
            retainList = new ArrayList(collection.size());

            if (debug) {
                log.debug("Filtering collection with " + collection.size()
                        + " elements");
            }

            if (permissionCacheOptimizer != null) {
                permissionCacheOptimizer.cachePermissionsFor(
                        rootObject.getAuthentication(), collection);
            }

            for (Object filterObject : (Collection) filterTarget) {
                rootObject.setFilterObject(filterObject);

                if (ExpressionUtils.evaluateAsBoolean(filterExpression, ctx)) {
                    retainList.add(filterObject);
                }
            }

            if (debug) {
                log.debug("Retaining elements: " + retainList);
            }

            collection.clear();
            collection.addAll(retainList);

            return filterTarget;
        }

        if (filterTarget.getClass().isArray()) {
            Object[] array = (Object[]) filterTarget;
            retainList = new ArrayList(array.length);

            if (debug) {
                log.debug("Filtering array with " + array.length + " elements");
            }

            if (permissionCacheOptimizer != null) {
                permissionCacheOptimizer.cachePermissionsFor(
                        rootObject.getAuthentication(), Arrays.asList(array));
            }

            for (Object o : array) {
                rootObject.setFilterObject(o);

                if (ExpressionUtils.evaluateAsBoolean(filterExpression, ctx)) {
                    retainList.add(o);
                }
            }

            if (debug) {
                log.debug("Retaining elements: " + retainList);
            }

            Object[] filtered = (Object[]) Array.newInstance(filterTarget.getClass()
                    .getComponentType(), retainList.size());
            for (int i = 0; i < retainList.size(); i++) {
                filtered[i] = retainList.get(i);
            }

            return filtered;
        }

        throw new IllegalArgumentException(
                "Filter target must be a collection or array type, but was "
                        + filterTarget);
    }

    /**
     * Sets the {@link AuthenticationTrustResolver} to be used. The default is
     * {@link AuthenticationTrustResolverImpl}.
     *
     * @param trustResolver the {@link AuthenticationTrustResolver} to use. Cannot be
     * null.
     */
    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        Assert.notNull(trustResolver, "trustResolver cannot be null");
        this.trustResolver = trustResolver;
    }

    /**
     * @return The current {@link AuthenticationTrustResolver}
     */
    protected AuthenticationTrustResolver getTrustResolver() {
        return trustResolver;
    }

    /**
     * Sets the {@link ParameterNameDiscoverer} to use. The default is
     * {@link DefaultSecurityParameterNameDiscoverer}.
     * @param parameterNameDiscoverer
     */
    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    /**
     * @return The current {@link ParameterNameDiscoverer}
     */
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return parameterNameDiscoverer;
    }

    public void setPermissionCacheOptimizer(
            PermissionCacheOptimizer permissionCacheOptimizer) {
        this.permissionCacheOptimizer = permissionCacheOptimizer;
    }

    @Override
    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        ((MethodSecurityExpressionOperations) ctx.getRootObject().getValue())
                .setReturnObject(returnObject);
    }

    /**
     * <p>
     * If null or empty, then no default role prefix is used.
     * </p>
     *
     * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
     */
    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    /**
     * @return The default role prefix
     */
    protected String getDefaultRolePrefix() {
        return defaultRolePrefix;
    }
}
