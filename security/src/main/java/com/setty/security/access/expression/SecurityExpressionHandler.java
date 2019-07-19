package com.setty.security.access.expression;

import com.setty.security.core.Authentication;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;

/**
 * @author HuSen
 * create on 2019/7/18 17:59
 */
public interface SecurityExpressionHandler<T> extends AopInfrastructureBean {

    /**
     * @return an expression parser for the expressions used by the implementation.
     */
    ExpressionParser getExpressionParser();

    /**
     * Provides an evaluation context in which to evaluate security expressions for the
     * invocation type.
     */
    EvaluationContext createEvaluationContext(Authentication authentication, T invocation);
}
