package com.setty.security.access.hierarchicalroles;

import org.springframework.util.Assert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/18 18:40
 */
public final class RoleHierarchyUtils {
    private RoleHierarchyUtils() {
    }

    /**
     * Converts the supplied {@link Map} of role name to implied role name(s) to a string
     * representation understood by {@link RoleHierarchyImpl#setHierarchy(String)}.
     * The map key is the role name and the map value is a {@link List} of implied role name(s).
     *
     * @param roleHierarchyMap the mapping(s) of role name to implied role name(s)
     * @return a string representation of a role hierarchy
     * @throws IllegalArgumentException if roleHierarchyMap is null or empty or if a role name is null or
     * empty or if an implied role name(s) is null or empty
     *
     */
    public static String roleHierarchyFromMap(Map<String, List<String>> roleHierarchyMap) {
        Assert.notEmpty(roleHierarchyMap, "roleHierarchyMap cannot be empty");

        StringWriter roleHierarchyBuffer = new StringWriter();
        PrintWriter roleHierarchyWriter = new PrintWriter(roleHierarchyBuffer);

        for (Map.Entry<String, List<String>> roleHierarchyEntry : roleHierarchyMap.entrySet()) {
            String role = roleHierarchyEntry.getKey();
            List<String> impliedRoles = roleHierarchyEntry.getValue();

            Assert.hasLength(role, "role name must be supplied");
            Assert.notEmpty(impliedRoles, "implied role name(s) cannot be empty");

            for (String impliedRole : impliedRoles) {
                String roleMapping = role + " > " + impliedRole;
                roleHierarchyWriter.println(roleMapping);
            }
        }

        return roleHierarchyBuffer.toString();
    }
}
