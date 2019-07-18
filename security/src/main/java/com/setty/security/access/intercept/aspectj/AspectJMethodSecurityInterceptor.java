package com.setty.security.access.intercept.aspectj;

import com.setty.security.access.intercept.InterceptorStatusToken;
import com.setty.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.aspectj.lang.JoinPoint;

/**
 * @author HuSen
 * create on 2019/7/12 16:17
 */
public final class AspectJMethodSecurityInterceptor extends MethodSecurityInterceptor {

    /**
     * Method that is suitable for user with @Aspect notation.
     *
     * @param jp The AspectJ joint point being invoked which requires a security decision
     * @return The returned value from the method invocation
     * @throws Throwable if the invocation throws one
     */
    public Object invoke(JoinPoint jp) throws Throwable {
        return super.invoke(new MethodInvocationAdapter(jp));
    }

    /**
     * Method that is suitable for user with traditional AspectJ-code aspects.
     *
     * @param jp The AspectJ joint point being invoked which requires a security decision
     * @param advisorProceed the advice-defined anonymous class that implements
     * {@code AspectJCallback} containing a simple {@code return proceed();} statement
     *
     * @return The returned value from the method invocation
     */
    public Object invoke(JoinPoint jp, AspectJCallback advisorProceed) {
        InterceptorStatusToken token = super
                .beforeInvocation(new MethodInvocationAdapter(jp));

        Object result;
        try {
            result = advisorProceed.proceedWithObject();
        }
        finally {
            super.finallyInvocation(token);
        }

        return super.afterInvocation(token, result);
    }
}
