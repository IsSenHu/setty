package com.setty.security.core.parameters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author HuSen
 * create on 2019/7/18 18:16
 */
@Slf4j
public class DefaultSecurityParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

    private static final String DATA_PARAM_CLASSNAME = "org.springframework.data.repository.query.Param";
    private static final boolean DATA_PARAM_PRESENT = ClassUtils.isPresent(
            DATA_PARAM_CLASSNAME,
            DefaultSecurityParameterNameDiscoverer.class.getClassLoader());

    /**
     * Creates a new instance with only the default {@link ParameterNameDiscoverer}
     * instances.
     */
    public DefaultSecurityParameterNameDiscoverer() {
        this(Collections.<ParameterNameDiscoverer> emptyList());
    }

    /**
     * Creates a new instance that first tries the passed in
     * {@link ParameterNameDiscoverer} instances.
     * @param parameterNameDiscovers the {@link ParameterNameDiscoverer} before trying the
     * defaults. Cannot be null.
     */
    @SuppressWarnings("unchecked")
    public DefaultSecurityParameterNameDiscoverer(
            List<? extends ParameterNameDiscoverer> parameterNameDiscovers) {
        Assert.notNull(parameterNameDiscovers, "parameterNameDiscovers cannot be null");
        for (ParameterNameDiscoverer discover : parameterNameDiscovers) {
            addDiscoverer(discover);
        }

        Set<String> annotationClassesToUse = new HashSet<>(2);
        annotationClassesToUse.add("org.springframework.security.access.method.P");
        annotationClassesToUse.add(P.class.getName());
        if (DATA_PARAM_PRESENT) {
            annotationClassesToUse.add(DATA_PARAM_CLASSNAME);
        }

        addDiscoverer(new AnnotationParameterNameDiscoverer(annotationClassesToUse));
        addDiscoverer(new DefaultParameterNameDiscoverer());
    }
}
