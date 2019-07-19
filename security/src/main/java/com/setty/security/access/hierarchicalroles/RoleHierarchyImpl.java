package com.setty.security.access.hierarchicalroles;

import com.setty.security.core.GrantedAuthority;
import com.setty.security.core.authority.AuthorityUtils;
import com.setty.security.core.authority.SimpleGrantedAuthority;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * @author HuSen
 * create on 2019/7/18 18:38
 */
@Slf4j
public class RoleHierarchyImpl implements RoleHierarchy {

    private String roleHierarchyStringRepresentation = null;

    /**
     * rolesReachableInOneStepMap is a Map that under the key of a specific role name
     * contains a set of all roles reachable from this role in 1 step
     */
    private Map<GrantedAuthority, Set<GrantedAuthority>> rolesReachableInOneStepMap = null;

    /**
     * rolesReachableInOneOrMoreStepsMap is a Map that under the key of a specific role
     * name contains a set of all roles reachable from this role in 1 or more steps
     */
    private Map<GrantedAuthority, Set<GrantedAuthority>> rolesReachableInOneOrMoreStepsMap = null;

    /**
     * Set the role hierarchy and pre-calculate for every role the set of all reachable
     * roles, i.e. all roles lower in the hierarchy of every given role. Pre-calculation
     * is done for performance reasons (reachable roles can then be calculated in O(1)
     * time). During pre-calculation, cycles in role hierarchy are detected and will cause
     * a <tt>CycleInRoleHierarchyException</tt> to be thrown.
     *
     * @param roleHierarchyStringRepresentation - String definition of the role hierarchy.
     */
    public void setHierarchy(String roleHierarchyStringRepresentation) {
        this.roleHierarchyStringRepresentation = roleHierarchyStringRepresentation;

        log.debug("setHierarchy() - The following role hierarchy was set: "
                + roleHierarchyStringRepresentation);

        buildRolesReachableInOneStepMap();
        buildRolesReachableInOneOrMoreStepsMap();
    }

    @Override
    public Collection<GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return AuthorityUtils.NO_AUTHORITIES;
        }

        Set<GrantedAuthority> reachableRoles = new HashSet<>();

        for (GrantedAuthority authority : authorities) {
            addReachableRoles(reachableRoles, authority);
            Set<GrantedAuthority> additionalReachableRoles = getRolesReachableInOneOrMoreSteps(
                    authority);
            if (additionalReachableRoles != null) {
                reachableRoles.addAll(additionalReachableRoles);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("getReachableGrantedAuthorities() - From the roles "
                    + authorities + " one can reach " + reachableRoles
                    + " in zero or more steps.");
        }

        List<GrantedAuthority> reachableRoleList = new ArrayList<>(
                reachableRoles.size());
        reachableRoleList.addAll(reachableRoles);

        return reachableRoleList;
    }

    // SEC-863
    private void addReachableRoles(Set<GrantedAuthority> reachableRoles,
                                   GrantedAuthority authority) {

        for (GrantedAuthority testAuthority : reachableRoles) {
            String testKey = testAuthority.getAuthority();
            if ((testKey != null) && (testKey.equals(authority.getAuthority()))) {
                return;
            }
        }
        reachableRoles.add(authority);
    }

    // SEC-863
    private Set<GrantedAuthority> getRolesReachableInOneOrMoreSteps(
            GrantedAuthority authority) {

        if (authority.getAuthority() == null) {
            return null;
        }

        for (GrantedAuthority testAuthority : this.rolesReachableInOneOrMoreStepsMap
                .keySet()) {
            String testKey = testAuthority.getAuthority();
            if ((testKey != null) && (testKey.equals(authority.getAuthority()))) {
                return this.rolesReachableInOneOrMoreStepsMap.get(testAuthority);
            }
        }

        return null;
    }

    /**
     * Parse input and build the map for the roles reachable in one step: the higher role
     * will become a key that references a set of the reachable lower roles.
     */
    private void buildRolesReachableInOneStepMap() {
        this.rolesReachableInOneStepMap = new HashMap<GrantedAuthority, Set<GrantedAuthority>>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new StringReader(this.roleHierarchyStringRepresentation))) {
            for (String readLine; (readLine = bufferedReader.readLine()) != null; ) {
                String[] roles = readLine.split(" > ");
                for (int i = 1; i < roles.length; i++) {
                    GrantedAuthority higherRole = new SimpleGrantedAuthority(
                            roles[i - 1].replaceAll("^\\s+|\\s+$", ""));
                    GrantedAuthority lowerRole = new SimpleGrantedAuthority(roles[i].replaceAll("^\\s+|\\s+$", ""));
                    Set<GrantedAuthority> rolesReachableInOneStepSet;
                    if (!this.rolesReachableInOneStepMap.containsKey(higherRole)) {
                        rolesReachableInOneStepSet = new HashSet<GrantedAuthority>();
                        this.rolesReachableInOneStepMap.put(higherRole, rolesReachableInOneStepSet);
                    } else {
                        rolesReachableInOneStepSet = this.rolesReachableInOneStepMap.get(higherRole);
                    }
                    addReachableRoles(rolesReachableInOneStepSet, lowerRole);
                    if (log.isDebugEnabled()) {
                        log.debug("buildRolesReachableInOneStepMap() - From role " + higherRole
                                + " one can reach role " + lowerRole + " in one step.");
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * For every higher role from rolesReachableInOneStepMap store all roles that are
     * reachable from it in the map of roles reachable in one or more steps. (Or throw a
     * CycleInRoleHierarchyException if a cycle in the role hierarchy definition is
     * detected)
     */
    private void buildRolesReachableInOneOrMoreStepsMap() {
        this.rolesReachableInOneOrMoreStepsMap = new HashMap<>();
        // iterate over all higher roles from rolesReachableInOneStepMap

        for (GrantedAuthority role : this.rolesReachableInOneStepMap.keySet()) {
            Set<GrantedAuthority> rolesToVisitSet = new HashSet<>();

            if (this.rolesReachableInOneStepMap.containsKey(role)) {
                rolesToVisitSet.addAll(this.rolesReachableInOneStepMap.get(role));
            }

            Set<GrantedAuthority> visitedRolesSet = new HashSet<>();

            while (!rolesToVisitSet.isEmpty()) {
                // take a role from the rolesToVisit set
                GrantedAuthority aRole = rolesToVisitSet.iterator().next();
                rolesToVisitSet.remove(aRole);
                addReachableRoles(visitedRolesSet, aRole);
                if (this.rolesReachableInOneStepMap.containsKey(aRole)) {
                    Set<GrantedAuthority> newReachableRoles = this.rolesReachableInOneStepMap
                            .get(aRole);

                    // definition of a cycle: you can reach the role you are starting from
                    if (rolesToVisitSet.contains(role)
                            || visitedRolesSet.contains(role)) {
                        throw new CycleInRoleHierarchyException();
                    } else {
                        // no cycle
                        rolesToVisitSet.addAll(newReachableRoles);
                    }
                }
            }
            this.rolesReachableInOneOrMoreStepsMap.put(role, visitedRolesSet);

            log.debug("buildRolesReachableInOneOrMoreStepsMap() - From role " + role
                    + " one can reach " + visitedRolesSet + " in one or more steps.");
        }

    }
}
