package com.setty.security.access.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author HuSen
 * create on 2019/7/12 15:32
 */
public abstract class AbstractAuthorizationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 260639880106578707L;

    /**
     * Construct the event, passing in the secure object being intercepted.
     *
     * @param secureObject the secure object
     */
    public AbstractAuthorizationEvent(Object secureObject) {
        super(secureObject);
    }
}
