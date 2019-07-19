package com.setty.security.access.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;

/**
 * @author HuSen
 * create on 2019/7/18 18:04
 */
public class ExpressionUtils {

    public static boolean evaluateAsBoolean(Expression expr, EvaluationContext ctx) {
        try {
            return ((Boolean) expr.getValue(ctx, Boolean.class)).booleanValue();
        }
        catch (EvaluationException e) {
            throw new IllegalArgumentException("Failed to evaluate expression '"
                    + expr.getExpressionString() + "'", e);
        }
    }
}
