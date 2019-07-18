package com.setty.security.access.expression.method;

import com.setty.security.access.ConfigAttribute;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/7/18 18:10
 */
public abstract class AbstractExpressionBasedMethodConfigAttribute implements ConfigAttribute {
    private final Expression filterExpression;
    private final Expression authorizeExpression;

    /**
     * Parses the supplied expressions as Spring-EL.
     */
    AbstractExpressionBasedMethodConfigAttribute(String filterExpression,
                                                 String authorizeExpression) throws ParseException {
        Assert.isTrue(filterExpression != null || authorizeExpression != null,
                "Filter and authorization Expressions cannot both be null");
        SpelExpressionParser parser = new SpelExpressionParser();
        this.filterExpression = filterExpression == null ? null : parser
                .parseExpression(filterExpression);
        this.authorizeExpression = authorizeExpression == null ? null : parser
                .parseExpression(authorizeExpression);
    }

    AbstractExpressionBasedMethodConfigAttribute(Expression filterExpression,
                                                 Expression authorizeExpression) throws ParseException {
        Assert.isTrue(filterExpression != null || authorizeExpression != null,
                "Filter and authorization Expressions cannot both be null");
        this.filterExpression = filterExpression == null ? null : filterExpression;
        this.authorizeExpression = authorizeExpression == null ? null
                : authorizeExpression;
    }

    Expression getFilterExpression() {
        return filterExpression;
    }

    Expression getAuthorizeExpression() {
        return authorizeExpression;
    }

    @Override
    public String getAttribute() {
        return null;
    }
}
