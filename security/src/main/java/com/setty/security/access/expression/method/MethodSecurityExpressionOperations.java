package com.setty.security.access.expression.method;

import com.setty.security.access.expression.SecurityExpressionOperations;

/**
 * @author HuSen
 * create on 2019/7/18 18:03
 */
public interface MethodSecurityExpressionOperations extends SecurityExpressionOperations {

    void setFilterObject(Object filterObject);

    Object getFilterObject();

    void setReturnObject(Object returnObject);

    Object getReturnObject();

    Object getThis();
}
