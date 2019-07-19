package com.setty.security.access.prepost;

import org.springframework.aop.framework.AopInfrastructureBean;

/**
 * @author HuSen
 * create on 2019/7/18 18:28
 */
public interface PrePostInvocationAttributeFactory extends AopInfrastructureBean {

    PreInvocationAttribute createPreInvocationAttribute(String preFilterAttribute,
                                                        String filterObject, String preAuthorizeAttribute);

    PostInvocationAttribute createPostInvocationAttribute(String postFilterAttribute,
                                                          String postAuthorizeAttribute);
}
