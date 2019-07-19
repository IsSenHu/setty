package com.setty.security.access;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 17:50
 */
public class SecurityConfig implements ConfigAttribute {

    private static final long serialVersionUID = -463366281635250042L;

    private final String attrib;

    // ~ Constructors
    // ===================================================================================================

    public SecurityConfig(String config) {
        Assert.hasText(config, "You must provide a configuration attribute");
        this.attrib = config;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigAttribute) {
            ConfigAttribute attr = (ConfigAttribute) obj;

            return this.attrib.equals(attr.getAttribute());
        }

        return false;
    }

    @Override
    public String getAttribute() {
        return this.attrib;
    }

    @Override
    public int hashCode() {
        return this.attrib.hashCode();
    }

    @Override
    public String toString() {
        return this.attrib;
    }

    public static List<ConfigAttribute> createListFromCommaDelimitedString(String access) {
        return createList(StringUtils.commaDelimitedListToStringArray(access));
    }

    public static List<ConfigAttribute> createList(String... attributeNames) {
        Assert.notNull(attributeNames, "You must supply an array of attribute names");
        List<ConfigAttribute> attributes = new ArrayList<>(
                attributeNames.length);

        for (String attribute : attributeNames) {
            attributes.add(new SecurityConfig(attribute.trim()));
        }

        return attributes;
    }
}
