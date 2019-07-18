package com.setty.security.access.hierarchicalroles;

/**
 * @author HuSen
 * create on 2019/7/18 18:35
 */
public class CycleInRoleHierarchyException extends RuntimeException {
    private static final long serialVersionUID = 7764116449223215708L;

    public CycleInRoleHierarchyException() {
        super("Exception thrown because of a cycle in the role hierarchy definition!");
    }
}
