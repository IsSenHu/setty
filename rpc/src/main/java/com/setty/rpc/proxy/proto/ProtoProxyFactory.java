package com.setty.rpc.proxy.proto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

/**
 * @author HuSen
 * create on 2019/7/4 13:33
 */
@Slf4j
@Data
public class ProtoProxyFactory implements FactoryBean<Object>, BeanPostProcessor {

    private Class<?> interfaceClass;

    @Override
    public Object getObject() {
        return new ProtoProxy().bind(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) throws BeansException {
        if (log.isDebugEnabled()) {
            log.debug("start initialization bean:{}", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, String beanName) throws BeansException {
        if (log.isDebugEnabled()) {
            log.debug("finish initialization bean:{}", beanName);
        }
        return bean;
    }
}
