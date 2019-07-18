package com.setty.security.access.intercept;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:47
 */
@Slf4j
public class MethodInvocationPrivilegeEvaluator implements InitializingBean {


    // ~ Instance fields
    // ================================================================================================

    private AbstractSecurityInterceptor securityInterceptor;

    // ~ Methods
    // ========================================================================================================

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(securityInterceptor, "SecurityInterceptor required");
    }

    public boolean isAllowed(MethodInvocation mi, Authentication authentication) {
        Assert.notNull(mi, "MethodInvocation required");
        Assert.notNull(mi.getMethod(),
                "MethodInvocation must provide a non-null getMethod()");

        Collection<ConfigAttribute> attrs = securityInterceptor
                .obtainSecurityMetadataSource().getAttributes(mi);

        if (attrs == null) {
            if (securityInterceptor.isRejectPublicInvocations()) {
                return false;
            }

            return true;
        }

        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            return false;
        }

        try {
            securityInterceptor.getAccessDecisionManager().decide(authentication, mi,
                    attrs);
        }
        catch (AccessDeniedException unauthorized) {
            if (log.isDebugEnabled()) {
                log.debug(mi.toString() + " denied for " + authentication.toString(),
                        unauthorized);
            }

            return false;
        }

        return true;
    }

    public void setSecurityInterceptor(AbstractSecurityInterceptor securityInterceptor) {
        Assert.notNull(securityInterceptor, "AbstractSecurityInterceptor cannot be null");
        Assert.isTrue(
                MethodInvocation.class.equals(securityInterceptor.getSecureObjectClass()),
                "AbstractSecurityInterceptor does not support MethodInvocations");
        Assert.notNull(securityInterceptor.getAccessDecisionManager(),
                "AbstractSecurityInterceptor must provide a non-null AccessDecisionManager");
        this.securityInterceptor = securityInterceptor;
    }
}
