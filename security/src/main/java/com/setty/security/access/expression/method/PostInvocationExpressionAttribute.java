package com.setty.security.access.expression.method;

import com.setty.security.access.prepost.PostInvocationAttribute;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;

/**
 * @author HuSen
 * create on 2019/7/18 18:30
 */
class PostInvocationExpressionAttribute extends AbstractExpressionBasedMethodConfigAttribute implements PostInvocationAttribute {

    PostInvocationExpressionAttribute(String filterExpression, String authorizeExpression)
            throws ParseException {
        super(filterExpression, authorizeExpression);
    }

    PostInvocationExpressionAttribute(Expression filterExpression,
                                      Expression authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Expression authorize = getAuthorizeExpression();
        Expression filter = getFilterExpression();
        sb.append("[authorize: '").append(
                authorize == null ? "null" : authorize.getExpressionString());
        sb.append("', filter: '")
                .append(filter == null ? "null" : filter.getExpressionString())
                .append("']");
        return sb.toString();
    }
}
