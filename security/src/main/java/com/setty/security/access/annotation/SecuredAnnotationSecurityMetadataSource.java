package com.setty.security.access.annotation;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.access.SecurityConfig;
import com.setty.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 17:54
 */
public class SecuredAnnotationSecurityMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    private AnnotationMetadataExtractor annotationExtractor;
    private Class<? extends Annotation> annotationType;

    public SecuredAnnotationSecurityMetadataSource() {
        this(new SecuredAnnotationMetadataExtractor());
    }

    public SecuredAnnotationSecurityMetadataSource(
            AnnotationMetadataExtractor annotationMetadataExtractor) {
        Assert.notNull(annotationMetadataExtractor, "annotationMetadataExtractor cannot be null");
        annotationExtractor = annotationMetadataExtractor;
        annotationType = (Class<? extends Annotation>) GenericTypeResolver
                .resolveTypeArgument(annotationExtractor.getClass(),
                        AnnotationMetadataExtractor.class);
        Assert.notNull(annotationType, () -> annotationExtractor.getClass().getName()
                + " must supply a generic parameter for AnnotationMetadataExtractor");
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return processAnnotation(AnnotationUtils.findAnnotation(clazz, annotationType));
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method,
                                                         Class<?> targetClass) {
        return processAnnotation(AnnotationUtils.findAnnotation(method, annotationType));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private Collection<ConfigAttribute> processAnnotation(Annotation a) {
        if (a == null) {
            return null;
        }

        return annotationExtractor.extractAttributes(a);
    }
}

class SecuredAnnotationMetadataExtractor implements AnnotationMetadataExtractor<Secured> {

    @Override
    public Collection<ConfigAttribute> extractAttributes(Secured secured) {
        String[] attributeTokens = secured.value();
        List<ConfigAttribute> attributes = new ArrayList<>(
                attributeTokens.length);

        for (String token : attributeTokens) {
            attributes.add(new SecurityConfig(token));
        }

        return attributes;
    }
}
