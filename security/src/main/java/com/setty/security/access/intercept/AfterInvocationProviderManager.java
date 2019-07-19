package com.setty.security.access.intercept;

import com.setty.security.access.AccessDeniedException;
import com.setty.security.access.AfterInvocationProvider;
import com.setty.security.access.ConfigAttribute;
import com.setty.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/18 18:45
 */
@Slf4j
public class AfterInvocationProviderManager implements AfterInvocationManager, InitializingBean {

    // ~ Instance fields
    // ================================================================================================

    private List<AfterInvocationProvider> providers;

    // ~ Methods
    // ========================================================================================================

    @Override
    public void afterPropertiesSet() throws Exception {
        checkIfValidList(this.providers);
    }

    private void checkIfValidList(List<?> listToCheck) {
        if ((listToCheck == null) || (listToCheck.size() == 0)) {
            throw new IllegalArgumentException(
                    "A list of AfterInvocationProviders is required");
        }
    }

    @Override
    public Object decide(Authentication authentication, Object object,
                         Collection<ConfigAttribute> config, Object returnedObject)
            throws AccessDeniedException {

        Object result = returnedObject;

        for (AfterInvocationProvider provider : providers) {
            result = provider.decide(authentication, object, config, result);
        }

        return result;
    }

    public List<AfterInvocationProvider> getProviders() {
        return this.providers;
    }

    public void setProviders(List<?> newList) {
        checkIfValidList(newList);
        providers = new ArrayList<>(newList.size());

        for (Object currentObject : newList) {
            Assert.isInstanceOf(AfterInvocationProvider.class, currentObject,
                    () -> "AfterInvocationProvider " + currentObject.getClass().getName()
                            + " must implement AfterInvocationProvider");
            providers.add((AfterInvocationProvider) currentObject);
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        for (AfterInvocationProvider provider : providers) {
            if (log.isDebugEnabled()) {
                log.debug("Evaluating " + attribute + " against " + provider);
            }

            if (provider.supports(attribute)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Iterates through all <code>AfterInvocationProvider</code>s and ensures each can
     * support the presented class.
     * <p>
     * If one or more providers cannot support the presented class, <code>false</code> is
     * returned.
     *
     * @param clazz the secure object class being queries
     *
     * @return if the <code>AfterInvocationProviderManager</code> can support the secure
     * object class, which requires every one of its <code>AfterInvocationProvider</code>s
     * to support the secure object class
     */
    @Override
    public boolean supports(Class<?> clazz) {
        for (AfterInvocationProvider provider : providers) {
            if (!provider.supports(clazz)) {
                return false;
            }
        }

        return true;
    }
}
