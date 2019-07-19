package com.setty.security.access.expression;

import com.setty.security.access.PermissionEvaluator;
import com.setty.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/7/18 18:09
 */
@Slf4j
public class DenyAllPermissionEvaluator implements PermissionEvaluator {

    /**
     * @return false always
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object target,
                                 Object permission) {
        log.warn("Denying user " + authentication.getName() + " permission '"
                + permission + "' on object " + target);
        return false;
    }

    /**
     * @return false always
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        log.warn("Denying user " + authentication.getName() + " permission '"
                + permission + "' on object with Id '" + targetId);
        return false;
    }
}
