package com.setty.security.access.expression.method;

import com.setty.security.access.prepost.PreInvocationAttribute;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;

/**
 * @author HuSen
 * create on 2019/7/18 18:30
 */
class PreInvocationExpressionAttribute extends AbstractExpressionBasedMethodConfigAttribute implements PreInvocationAttribute {

    private final String filterTarget;

    PreInvocationExpressionAttribute(String filterExpression, String filterTarget,
                                     String authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);

        this.filterTarget = filterTarget;
    }

    PreInvocationExpressionAttribute(Expression filterExpression, String filterTarget,
                                     Expression authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);

        this.filterTarget = filterTarget;
    }

    /**
     * The parameter name of the target argument (must be a Collection) to which filtering
     * will be applied.
     *
     * @return the method parameter name
     */
    String getFilterTarget() {
        return filterTarget;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Expression authorize = getAuthorizeExpression();
        Expression filter = getFilterExpression();
        sb.append("[authorize: '").append(
                authorize == null ? "null" : authorize.getExpressionString());
        sb.append("', filter: '").append(
                filter == null ? "null" : filter.getExpressionString());
        sb.append("', filterTarget: '").append(filterTarget).append("']");
        return sb.toString();
    }
}
