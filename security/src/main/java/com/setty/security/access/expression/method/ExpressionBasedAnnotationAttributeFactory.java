package com.setty.security.access.expression.method;

import com.setty.security.access.prepost.PostInvocationAttribute;
import com.setty.security.access.prepost.PreInvocationAttribute;
import com.setty.security.access.prepost.PrePostInvocationAttributeFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;

/**
 * @author HuSen
 * create on 2019/7/18 18:27
 */
public class ExpressionBasedAnnotationAttributeFactory implements PrePostInvocationAttributeFactory {
    private final Object parserLock = new Object();
    private ExpressionParser parser;
    private MethodSecurityExpressionHandler handler;

    public ExpressionBasedAnnotationAttributeFactory(
            MethodSecurityExpressionHandler handler) {
        this.handler = handler;
    }

    @Override
    public PreInvocationAttribute createPreInvocationAttribute(String preFilterAttribute,
                                                               String filterObject, String preAuthorizeAttribute) {
        try {
            // TODO: Optimization of permitAll
            ExpressionParser parser = getParser();
            Expression preAuthorizeExpression = preAuthorizeAttribute == null ? parser
                    .parseExpression("permitAll") : parser
                    .parseExpression(preAuthorizeAttribute);
            Expression preFilterExpression = preFilterAttribute == null ? null : parser
                    .parseExpression(preFilterAttribute);
            return new PreInvocationExpressionAttribute(preFilterExpression,
                    filterObject, preAuthorizeExpression);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse expression '"
                    + e.getExpressionString() + "'", e);
        }
    }

    @Override
    public PostInvocationAttribute createPostInvocationAttribute(
            String postFilterAttribute, String postAuthorizeAttribute) {
        try {
            ExpressionParser parser = getParser();
            Expression postAuthorizeExpression = postAuthorizeAttribute == null ? null
                    : parser.parseExpression(postAuthorizeAttribute);
            Expression postFilterExpression = postFilterAttribute == null ? null : parser
                    .parseExpression(postFilterAttribute);

            if (postFilterExpression != null || postAuthorizeExpression != null) {
                return new PostInvocationExpressionAttribute(postFilterExpression,
                        postAuthorizeExpression);
            }
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse expression '"
                    + e.getExpressionString() + "'", e);
        }

        return null;
    }

    /**
     * Delay the lookup of the {@link ExpressionParser} to prevent SEC-2136
     *
     * @return
     */
    private ExpressionParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        synchronized (parserLock) {
            this.parser = handler.getExpressionParser();
            this.handler = null;
        }
        return this.parser;
    }
}
