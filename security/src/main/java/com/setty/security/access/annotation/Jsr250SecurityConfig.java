package com.setty.security.access.annotation;

import com.setty.security.access.SecurityConfig;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;

/**
 * @author HuSen
 * create on 2019/7/18 17:49
 */
public class Jsr250SecurityConfig extends SecurityConfig {

    public static final Jsr250SecurityConfig PERMIT_ALL_ATTRIBUTE = new Jsr250SecurityConfig(
            PermitAll.class.getName());
    public static final Jsr250SecurityConfig DENY_ALL_ATTRIBUTE = new Jsr250SecurityConfig(
            DenyAll.class.getName());

    public Jsr250SecurityConfig(String role) {
        super(role);
    }
}
