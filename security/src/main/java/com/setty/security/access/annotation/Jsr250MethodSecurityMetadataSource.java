package com.setty.security.access.annotation;

import com.setty.security.access.ConfigAttribute;
import com.setty.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 17:42
 */
public class Jsr250MethodSecurityMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    private String defaultRolePrefix = "ROLE_";

    /**
     * <p>
     * Sets the default prefix to be added to {@link RolesAllowed}. For example, if
     * {@code @RolesAllowed("ADMIN")} or {@code @RolesAllowed("ADMIN")} is used, then the
     * role ROLE_ADMIN will be used when the defaultRolePrefix is "ROLE_" (default).
     * </p>
     *
     * <p>
     * If null or empty, then no default role prefix is used.
     * </p>
     *
     * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
     */
    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return processAnnotations(clazz.getAnnotations());
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method,
                                                         Class<?> targetClass) {
        return processAnnotations(AnnotationUtils.getAnnotations(method));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private List<ConfigAttribute> processAnnotations(Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        List<ConfigAttribute> attributes = new ArrayList<>();

        for (Annotation a : annotations) {
            if (a instanceof DenyAll) {
                attributes.add(Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE);
                return attributes;
            }
            if (a instanceof PermitAll) {
                attributes.add(Jsr250SecurityConfig.PERMIT_ALL_ATTRIBUTE);
                return attributes;
            }
            if (a instanceof RolesAllowed) {
                RolesAllowed ra = (RolesAllowed) a;

                for (String allowed : ra.value()) {
                    String defaultedAllowed = getRoleWithDefaultPrefix(allowed);
                    attributes.add(new Jsr250SecurityConfig(defaultedAllowed));
                }
                return attributes;
            }
        }
        return null;
    }

    private String getRoleWithDefaultPrefix(String role) {
        if (role == null) {
            return role;
        }
        if (defaultRolePrefix == null || defaultRolePrefix.length() == 0) {
            return role;
        }
        if (role.startsWith(defaultRolePrefix)) {
            return role;
        }
        return defaultRolePrefix + role;
    }
}
