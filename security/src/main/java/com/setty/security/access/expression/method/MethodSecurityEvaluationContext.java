package com.setty.security.access.expression.method;

import com.setty.security.core.Authentication;
import com.setty.security.core.parameters.DefaultSecurityParameterNameDiscoverer;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author HuSen
 * create on 2019/7/18 18:19
 */
@Slf4j
class MethodSecurityEvaluationContext extends StandardEvaluationContext {
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private final MethodInvocation mi;
    private boolean argumentsAdded;
    public MethodSecurityEvaluationContext(Authentication user, MethodInvocation mi) {
        this(user, mi, new DefaultSecurityParameterNameDiscoverer());
    }

    public MethodSecurityEvaluationContext(Authentication user, MethodInvocation mi,
                                           ParameterNameDiscoverer parameterNameDiscoverer) {
        this.mi = mi;
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);

        if (variable != null) {
            return variable;
        }

        if (!argumentsAdded) {
            addArgumentsAsVariables();
            argumentsAdded = true;
        }

        variable = super.lookupVariable(name);

        if (variable != null) {
            return variable;
        }

        return null;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    private void addArgumentsAsVariables() {
        Object[] args = mi.getArguments();

        if (args.length == 0) {
            return;
        }

        Object targetObject = mi.getThis();
        // SEC-1454
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(targetObject);

        if (targetClass == null) {
            // TODO: Spring should do this, but there's a bug in ultimateTargetClass()
            // which returns null
            targetClass = targetObject.getClass();
        }

        Method method = AopUtils.getMostSpecificMethod(mi.getMethod(), targetClass);
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

        if (paramNames == null) {
            log.warn("Unable to resolve method parameter names for method: "
                    + method
                    + ". Debug symbol information is required if you are using parameter names in expressions.");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            super.setVariable(paramNames[i], args[i]);
        }
    }
}
