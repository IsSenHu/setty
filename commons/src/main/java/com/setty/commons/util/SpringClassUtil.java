package com.setty.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author HuSen
 * create on 2019/7/4 14:03
 */
@Slf4j
public class SpringClassUtil {

    public static void loadClassByClass(Class[] packageClasses, ResourceLoader resourceLoader, List<Class<?>> classes, BiFunction<Class, String, Boolean> filter) {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourceLoader);
        for (Class packageClass : packageClasses) {
            try {
                Resource[] resources = resolver.getResources("classpath*:".concat(packageClass.getPackage().getName().replace(".", "/").concat("/**/*.class")));
                for (Resource resource : resources) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    // 过滤掉packageClass
                    if (filter != null && filter.apply(packageClass, className)) {
                        continue;
                    }
                    Class<?> forName = Class.forName(className);
                    classes.add(forName);
                }
            } catch (Exception e) {
                log.error("load packageClasses {} error", packageClass);
            }
        }
    }

    public static void loadClassByPackage(String[] packages, ResourceLoader resourceLoader, List<Class<?>> classes) {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourceLoader);
        for (String aPackage : packages) {
            try {
                Resource[] resources = resolver.getResources("classpath*:".concat(aPackage.replace(".", "/").concat("/**/*.class")));
                for (Resource resource : resources) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> forName = Class.forName(className);
                    classes.add(forName);
                }
            } catch (Exception e) {
                log.error("load package {} error", aPackage);
            }
        }
    }
}
