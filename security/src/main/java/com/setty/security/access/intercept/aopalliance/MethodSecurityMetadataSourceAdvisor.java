package com.setty.security.access.intercept.aopalliance;

import com.setty.security.access.method.MethodSecurityMetadataSource;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 18:41
 */
public class MethodSecurityMetadataSourceAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    // ~ Instance fields
    // ================================================================================================

    private transient MethodSecurityMetadataSource attributeSource;
    private transient MethodInterceptor interceptor;
    private final Pointcut pointcut = new MethodSecurityMetadataSourcePointcut();
    private BeanFactory beanFactory;
    private final String adviceBeanName;
    private final String metadataSourceBeanName;
    private transient volatile Object adviceMonitor = new Object();

    // ~ Constructors
    // ===================================================================================================

    /**
     * Alternative constructor for situations where we want the advisor decoupled from the
     * advice. Instead the advice bean name should be set. This prevents eager
     * instantiation of the interceptor (and hence the AuthenticationManager). See
     * SEC-773, for example. The metadataSourceBeanName is used rather than a direct
     * reference to support serialization via a bean factory lookup.
     *
     * @param adviceBeanName name of the MethodSecurityInterceptor bean
     * @param attributeSource the SecurityMetadataSource (should be the same as the one
     * used on the interceptor)
     * @param attributeSourceBeanName the bean name of the attributeSource (required for
     * serialization)
     */
    public MethodSecurityMetadataSourceAdvisor(String adviceBeanName,
                                               MethodSecurityMetadataSource attributeSource, String attributeSourceBeanName) {
        Assert.notNull(adviceBeanName, "The adviceBeanName cannot be null");
        Assert.notNull(attributeSource, "The attributeSource cannot be null");
        Assert.notNull(attributeSourceBeanName,
                "The attributeSourceBeanName cannot be null");

        this.adviceBeanName = adviceBeanName;
        this.attributeSource = attributeSource;
        this.metadataSourceBeanName = attributeSourceBeanName;
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        synchronized (this.adviceMonitor) {
            if (interceptor == null) {
                Assert.notNull(adviceBeanName,
                        "'adviceBeanName' must be set for use with bean factory lookup.");
                Assert.state(beanFactory != null,
                        "BeanFactory must be set to resolve 'adviceBeanName'");
                interceptor = beanFactory.getBean(this.adviceBeanName,
                        MethodInterceptor.class);
            }
            return interceptor;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void readObject(ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        ois.defaultReadObject();
        adviceMonitor = new Object();
        attributeSource = beanFactory.getBean(metadataSourceBeanName,
                MethodSecurityMetadataSource.class);
    }

    // ~ Inner Classes
    // ==================================================================================================

    class MethodSecurityMetadataSourcePointcut extends StaticMethodMatcherPointcut
            implements Serializable {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Method m, Class targetClass) {
            Collection attributes = attributeSource.getAttributes(m, targetClass);
            return attributes != null && !attributes.isEmpty();
        }
    }
}
