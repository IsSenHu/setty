package com.setty.security.access.intercept.aopalliance;

import com.setty.security.access.SecurityMetadataSource;
import com.setty.security.access.intercept.AbstractSecurityInterceptor;
import com.setty.security.access.intercept.InterceptorStatusToken;
import com.setty.security.access.method.MethodSecurityMetadataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author HuSen
 * create on 2019/7/12 16:02
 */
public class MethodSecurityInterceptor extends AbstractSecurityInterceptor implements MethodInterceptor {

    // ~ Instance fields
    // ================================================================================================

    private MethodSecurityMetadataSource securityMetadataSource;

    // ~ Methods
    // ========================================================================================================

    @Override
    public Class<?> getSecureObjectClass() {
        return MethodInvocation.class;
    }

    /**
     * This method should be used to enforce security on a <code>MethodInvocation</code>.
     *
     * @param mi The method being invoked which requires a security decision
     *
     * @return The returned value from the method invocation (possibly modified by the
     * {@code AfterInvocationManager}).
     *
     * @throws Throwable if any error occurs
     */
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        InterceptorStatusToken token = super.beforeInvocation(mi);

        Object result;
        try {
            result = mi.proceed();
        }
        finally {
            super.finallyInvocation(token);
        }
        return super.afterInvocation(token, result);
    }

    public MethodSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(MethodSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }
}
