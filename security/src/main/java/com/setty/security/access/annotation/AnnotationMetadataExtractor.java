package com.setty.security.access.annotation;

import com.setty.security.access.ConfigAttribute;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author HuSen
 * create on 2019/7/18 17:36
 */
public interface AnnotationMetadataExtractor<A extends Annotation> {

    Collection<? extends ConfigAttribute> extractAttributes(A securityAnnotation);
}
