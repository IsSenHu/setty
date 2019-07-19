package com.setty.security.access.prepost;

import com.setty.security.core.Authentication;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;

/**
 * @author HuSen
 * create on 2019/7/18 18:33
 */
public interface PreInvocationAuthorizationAdvice extends AopInfrastructureBean {

    /**
     * The "before" advice which should be executed to perform any filtering necessary and
     * to decide whether the method call is authorised.
     *
     * @param authentication the information on the principal on whose account the
     * decision should be made
     * @param mi the method invocation being attempted
     * @param preInvocationAttribute the attribute built from the @PreFilter and @PostFilter
     * annotations.
     * @return true if authorised, false otherwise
     */
    boolean before(Authentication authentication, MethodInvocation mi,
                   PreInvocationAttribute preInvocationAttribute);
}
