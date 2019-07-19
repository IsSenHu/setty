package com.setty.security.core.parameters;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author HuSen
 * create on 2019/7/18 18:18
 */
public class AnnotationParameterNameDiscoverer implements ParameterNameDiscoverer {
    private final Set<String> annotationClassesToUse;

    public AnnotationParameterNameDiscoverer(String... annotationClassToUse) {
        this(new HashSet<>(Arrays.asList(annotationClassToUse)));
    }

    public AnnotationParameterNameDiscoverer(Set<String> annotationClassesToUse) {
        Assert.notEmpty(annotationClassesToUse,
                "annotationClassesToUse cannot be null or empty");
        this.annotationClassesToUse = annotationClassesToUse;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.core.ParameterNameDiscoverer#getParameterNames(java
     * .lang.reflect.Method)
     */
    @Override
    public String[] getParameterNames(Method method) {
        Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);
        String[] paramNames = lookupParameterNames(METHOD_METHODPARAM_FACTORY,
                originalMethod);
        if (paramNames != null) {
            return paramNames;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?>[] interfaces = declaringClass.getInterfaces();
        for (Class<?> intrfc : interfaces) {
            Method intrfcMethod = ReflectionUtils.findMethod(intrfc, method.getName(),
                    method.getParameterTypes());
            if (intrfcMethod != null) {
                return lookupParameterNames(METHOD_METHODPARAM_FACTORY, intrfcMethod);
            }
        }
        return paramNames;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.core.ParameterNameDiscoverer#getParameterNames(java
     * .lang.reflect.Constructor)
     */
    @Override
    public String[] getParameterNames(Constructor<?> constructor) {
        return lookupParameterNames(CONSTRUCTOR_METHODPARAM_FACTORY, constructor);
    }

    /**
     * Gets the parameter names or null if not found.
     *
     * @param parameterNameFactory the {@link ParameterNameFactory} to use
     * @param t the {@link AccessibleObject} to find the parameter names on (i.e. Method
     * or Constructor)
     * @return the parameter names or null
     */
    private <T extends AccessibleObject> String[] lookupParameterNames(
            ParameterNameFactory<T> parameterNameFactory, T t) {
        Annotation[][] parameterAnnotations = parameterNameFactory.findParameterAnnotations(t);
        int parameterCount = parameterAnnotations.length;
        String[] paramNames = new String[parameterCount];
        boolean found = false;
        for (int i = 0; i < parameterCount; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            String parameterName = findParameterName(annotations);
            if (parameterName != null) {
                found = true;
                paramNames[i] = parameterName;
            }
        }
        return found ? paramNames : null;
    }

    /**
     * Finds the parameter name from the provided {@link Annotation}s or null if it could
     * not find it. The search is done by looking at the value property of the
     * {@link #annotationClassesToUse}.
     *
     * @param parameterAnnotations the {@link Annotation}'s to search.
     * @return
     */
    private String findParameterName(Annotation[] parameterAnnotations) {
        for (Annotation paramAnnotation : parameterAnnotations) {
            if (annotationClassesToUse.contains(paramAnnotation.annotationType()
                    .getName())) {
                return (String) AnnotationUtils.getValue(paramAnnotation, "value");
            }
        }
        return null;
    }

    private static final ParameterNameFactory<Constructor<?>> CONSTRUCTOR_METHODPARAM_FACTORY = new ParameterNameFactory<Constructor<?>>() {

        @Override
        public Annotation[][] findParameterAnnotations(Constructor<?> constructor) {
            return constructor.getParameterAnnotations();
        }
    };

    private static final ParameterNameFactory<Method> METHOD_METHODPARAM_FACTORY = new ParameterNameFactory<Method>() {

        @Override
        public Annotation[][] findParameterAnnotations(Method method) {
            return method.getParameterAnnotations();
        }
    };

    /**
     * Strategy interface for looking up the parameter names.
     *
     * @author Rob Winch
     * @since 3.2
     *
     * @param <T> the type to inspect (i.e. {@link Method} or {@link Constructor})
     */
    private interface ParameterNameFactory<T extends AccessibleObject> {

        /**
         * Gets the {@link Annotation}s at a specified index
         * @param t
         * @return
         */
        Annotation[][] findParameterAnnotations(T t);
    }
}
