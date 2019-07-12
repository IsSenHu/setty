package com.setty.security.access.event;

/**
 * @author HuSen
 * create on 2019/7/12 15:33
 */
public class PublicInvocationEvent extends AbstractAuthorizationEvent {

    private static final long serialVersionUID = 2050663869605479890L;

    /**
     * Construct the event, passing in the public secure object.
     *
     * @param secureObject the public secure object
     */
    public PublicInvocationEvent(Object secureObject) {
        super(secureObject);
    }
}
