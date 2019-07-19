package com.setty.security.access.expression;

import com.setty.security.access.PermissionEvaluator;
import com.setty.security.access.hierarchicalroles.RoleHierarchy;
import com.setty.security.core.Authentication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/7/18 18:08
 */
public abstract class AbstractSecurityExpressionHandler<T> implements SecurityExpressionHandler<T>, ApplicationContextAware {
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private BeanResolver br;
    private RoleHierarchy roleHierarchy;
    private PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();

    @Override
    public final ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public final void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "expressionParser cannot be null");
        this.expressionParser = expressionParser;
    }

    /**
     * Invokes the internal template methods to create {@code StandardEvaluationContext}
     * and {@code SecurityExpressionRoot} objects.
     *
     * @param authentication the current authentication object
     * @param invocation the invocation (filter, method, channel)
     * @return the context object for use in evaluating the expression, populated with a
     * suitable root object.
     */
    @Override
    public final EvaluationContext createEvaluationContext(Authentication authentication,
                                                           T invocation) {
        SecurityExpressionOperations root = createSecurityExpressionRoot(authentication,
                invocation);
        StandardEvaluationContext ctx = createEvaluationContextInternal(authentication,
                invocation);
        ctx.setBeanResolver(br);
        ctx.setRootObject(root);

        return ctx;
    }

    /**
     * Override to create a custom instance of {@code StandardEvaluationContext}.
     * <p>
     * The returned object will have a {@code SecurityExpressionRootPropertyAccessor}
     * added, allowing beans in the {@code ApplicationContext} to be accessed via
     * expression properties.
     *
     * @param authentication the current authentication object
     * @param invocation the invocation (filter, method, channel)
     * @return A {@code StandardEvaluationContext} or potentially a custom subclass if
     * overridden.
     */
    protected StandardEvaluationContext createEvaluationContextInternal(
            Authentication authentication, T invocation) {
        return new StandardEvaluationContext();
    }

    /**
     * Implement in order to create a root object of the correct type for the supported
     * invocation type.
     *
     * @param authentication the current authentication object
     * @param invocation the invocation (filter, method, channel)
     * @return the object wh
     */
    protected abstract SecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, T invocation);

    protected RoleHierarchy getRoleHierarchy() {
        return roleHierarchy;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    protected PermissionEvaluator getPermissionEvaluator() {
        return permissionEvaluator;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        br = new BeanFactoryResolver(applicationContext);
    }
}
