package com.setty.security.access.expression.method;

import com.setty.security.access.expression.SecurityExpressionHandler;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * @author HuSen
 * create on 2019/7/18 18:02
 */
public interface MethodSecurityExpressionHandler extends SecurityExpressionHandler<MethodInvocation> {

    /**
     * Filters a target collection or array. Only applies to method invocations.
     *
     * @param filterTarget the array or collection to be filtered.
     * @param filterExpression the expression which should be used as the filter
     * condition. If it returns false on evaluation, the object will be removed from the
     * returned collection
     * @param ctx the current evaluation context (as created through a call to
     * {@link #createEvaluationContext(org.springframework.security.core.Authentication, Object)}
     * @return the filtered collection or array
     */
    Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx);

    /**
     * Used to inform the expression system of the return object for the given evaluation
     * context. Only applies to method invocations.
     *
     * @param returnObject the return object value
     * @param ctx the context within which the object should be set (as created through a
     * call to
     * {@link #createEvaluationContext(org.springframework.security.core.Authentication, Object)}
     */
    void setReturnObject(Object returnObject, EvaluationContext ctx);
}
