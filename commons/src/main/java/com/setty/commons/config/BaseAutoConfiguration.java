package com.setty.commons.config;

import com.setty.commons.properties.BaseProperties;
import com.setty.commons.util.result.ResultMsgFactory;
import com.setty.commons.util.spring.SpringBeanUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/7/8
 */
@EnableConfigurationProperties(BaseProperties.class)
public class BaseAutoConfiguration implements ApplicationContextAware, ApplicationListener<ApplicationStartedEvent> {

    private final BaseProperties bp;

    @Autowired
    public BaseAutoConfiguration(BaseProperties bp) {
        this.bp = bp;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.setContext(applicationContext);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent applicationStartedEvent) {
        Assert.isTrue(bp.getAppCode() > 0, "appCode can not be zero");
        ResultMsgFactory.setAppCode(bp.getAppCode());
    }
}
