package com.setty.security.access.intercept.aopalliance;

import com.setty.security.access.SecurityMetadataSource;
import com.setty.security.access.intercept.AbstractSecurityInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author HuSen
 * create on 2019/7/12 16:02
 */
public class MethodSecurityInterceptor extends AbstractSecurityInterceptor implements MethodInterceptor {

    @Override
    public Class<?> getSecureObjectClass() {
        return null;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
