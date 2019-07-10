package com.setty.rpc.config.proto.registry;

import com.setty.commons.util.spring.SpringClassUtil;
import com.setty.rpc.annotation.proto.EnableProtoClient;
import com.setty.rpc.annotation.proto.ProtoClient;
import com.setty.rpc.proxy.proto.ProtoProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/4 9:35
 */
@Slf4j
@ConditionalOnBean(annotation = EnableProtoClient.class)
public class ProtoClientBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, ApplicationContextAware {

    private ResourceLoader resourceLoader;
    private List<Class<?>> clientClasses = new ArrayList<>();
    private ApplicationContext context;

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // 加载类
        loadClientClass();
        // 注册加载的类的动态代理对象
        registryClient(beanDefinitionRegistry);
    }

    /**
     * 注册加载的类的动态代理对象
     *
     * @param beanDefinitionRegistry BeanDefinitionRegistry
     */
    private void registryClient(@NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        log.info("开始注册 ProtoClientServer 动态代理类 ^_^ ^_^ ^_^");
        clientClasses.forEach(pc -> {
            ProtoClient protoClient = AnnotationUtils.findAnnotation(pc, ProtoClient.class);
            if (protoClient != null) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProtoProxyFactory.class);
                builder.addPropertyValue("interfaceClass", pc);
                builder.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), registryName(pc.getSimpleName()));
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanDefinitionRegistry);
            }
        });
        log.info("注册完成 ProtoClientServer 动态代理类:{}", clientClasses);
    }

    /**
     * 加载类
     */
    private void loadClientClass() {
        log.info("开始加载 ProtoClientServer 类 ^_^ ^_^ ^_^");
        // 获取到所有的EnableProtoClient注解的类
        Map<String, Object> clientsWithAnnotation = context.getBeansWithAnnotation(EnableProtoClient.class);
        clientsWithAnnotation.values().forEach(epc -> {
            // 迭代获取这些注解属性
            EnableProtoClient enableProtoClient = AnnotationUtils.findAnnotation(epc.getClass(), EnableProtoClient.class);
            if (enableProtoClient != null) {
                // 获取指定路径下的所有类
                Class[] packageClasses = enableProtoClient.packageClasses();
                SpringClassUtil.loadClassByClass(packageClasses, resourceLoader, clientClasses, (c, n) -> c.getName().equals(n));
                String[] packages = enableProtoClient.packages();
                SpringClassUtil.loadClassByPackage(packages, resourceLoader, clientClasses);
            }
        });
        log.info("加载 ProtoClientServer 类完成:{}", clientClasses);
    }

    /**
     * 生成注册的名称
     *
     * @param name 类简单名
     * @return beanName
     */
    private static String registryName(String name) {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {}

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
