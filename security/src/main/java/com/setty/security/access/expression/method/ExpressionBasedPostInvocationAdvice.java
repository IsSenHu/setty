package com.setty.security.access.expression.method;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.expression.ExpressionUtils;
import com.setty.security.access.prepost.PostInvocationAttribute;
import com.setty.security.access.prepost.PostInvocationAuthorizationAdvice;
import com.setty.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * @author HuSen
 * create on 2019/7/18 18:31
 */
@Slf4j
public class ExpressionBasedPostInvocationAdvice implements PostInvocationAuthorizationAdvice {
    private final MethodSecurityExpressionHandler expressionHandler;

    public ExpressionBasedPostInvocationAdvice(
            MethodSecurityExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }

    @Override
    public Object after(Authentication authentication, MethodInvocation mi,
                        PostInvocationAttribute postAttr, Object returnedObject)
            throws AccessDeniedException {
        PostInvocationExpressionAttribute pia = (PostInvocationExpressionAttribute) postAttr;
        EvaluationContext ctx = expressionHandler.createEvaluationContext(authentication,
                mi);
        Expression postFilter = pia.getFilterExpression();
        Expression postAuthorize = pia.getAuthorizeExpression();

        if (postFilter != null) {
            if (log.isDebugEnabled()) {
                log.debug("Applying PostFilter expression " + postFilter);
            }

            if (returnedObject != null) {
                returnedObject = expressionHandler
                        .filter(returnedObject, postFilter, ctx);
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug("Return object is null, filtering will be skipped");
                }
            }
        }

        expressionHandler.setReturnObject(returnedObject, ctx);

        if (postAuthorize != null
                && !ExpressionUtils.evaluateAsBoolean(postAuthorize, ctx)) {
            if (log.isDebugEnabled()) {
                log.debug("PostAuthorize expression rejected access");
            }
            throw new AccessDeniedException("Access is denied");
        }

        return returnedObject;
    }
}
