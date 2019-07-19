package com.setty.security.access.prepost;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.core.Authentication;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;

/**
 * @author HuSen
 * create on 2019/7/18 18:32
 */
public interface PostInvocationAuthorizationAdvice extends AopInfrastructureBean {

    Object after(Authentication authentication, MethodInvocation mi,
                 PostInvocationAttribute pia, Object returnedObject)
            throws AccessDeniedException;
}
