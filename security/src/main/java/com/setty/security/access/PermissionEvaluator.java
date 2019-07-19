package com.setty.security.access;

import com.setty.security.core.Authentication;
import org.springframework.aop.framework.AopInfrastructureBean;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/7/18 18:07
 */
public interface PermissionEvaluator extends AopInfrastructureBean {

    /**
     *
     * @param authentication represents the user in question. Should not be null.
     * @param targetDomainObject the domain object for which permissions should be
     * checked. May be null in which case implementations should return false, as the null
     * condition can be checked explicitly in the expression.
     * @param permission a representation of the permission object as supplied by the
     * expression system. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(Authentication authentication, Object targetDomainObject,
                          Object permission);

    /**
     * Alternative method for evaluating a permission where only the identifier of the
     * target object is available, rather than the target instance itself.
     *
     * @param authentication represents the user in question. Should not be null.
     * @param targetId the identifier for the object instance (usually a Long)
     * @param targetType a String representing the target's type (usually a Java
     * classname). Not null.
     * @param permission a representation of the permission object as supplied by the
     * expression system. Not null.
     * @return true if the permission is granted, false otherwise
     */
    boolean hasPermission(Authentication authentication, Serializable targetId,
                          String targetType, Object permission);
}
