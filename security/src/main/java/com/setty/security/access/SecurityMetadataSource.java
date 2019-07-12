package com.setty.security.access;

import com.setty.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.aop.framework.AopInfrastructureBean;

import java.util.Collection;

/**
 * Implemented by classes that store and can identify the {@link ConfigAttribute}s that
 * applies to a given secure object invocation.
 *
 * @author HuSen
 * create on 2019/7/12 15:22
 */
public interface SecurityMetadataSource extends AopInfrastructureBean {

    /**
     * Accesses the {@code ConfigAttribute}s that apply to a given secure object.
     *
     * @param object the object being secured
     *
     * @return the attributes that apply to the passed in secured object. Should return an
     * empty collection if there are no applicable attributes.
     *
     * @throws IllegalArgumentException if the passed object is not of a type supported by
     * the <code>SecurityMetadataSource</code> implementation
     */
    Collection<ConfigAttribute> getAttributes(Object object)
            throws IllegalArgumentException;

    /**
     * If available, returns all of the {@code ConfigAttribute}s defined by the
     * implementing class.
     * <p>
     * This is used by the {@link AbstractSecurityInterceptor} to perform startup time
     * validation of each {@code ConfigAttribute} configured against it.
     *
     * @return the {@code ConfigAttribute}s or {@code null} if unsupported
     */
    Collection<ConfigAttribute> getAllConfigAttributes();

    /**
     * Indicates whether the {@code SecurityMetadataSource} implementation is able to
     * provide {@code ConfigAttribute}s for the indicated secure object type.
     *
     * @param clazz the class that is being queried
     *
     * @return true if the implementation can process the indicated class
     */
    boolean supports(Class<?> clazz);
}
