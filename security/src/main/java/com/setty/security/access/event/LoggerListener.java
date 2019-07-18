package com.setty.security.access.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author HuSen
 * create on 2019/7/18 17:57
 */
@Slf4j
public class LoggerListener implements ApplicationListener<AbstractAuthorizationEvent> {

    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthenticationCredentialsNotFoundEvent) {
            AuthenticationCredentialsNotFoundEvent authEvent = (AuthenticationCredentialsNotFoundEvent) event;

            if (log.isWarnEnabled()) {
                log.warn("Security interception failed due to: "
                        + authEvent.getCredentialsNotFoundException()
                        + "; secure object: " + authEvent.getSource()
                        + "; configuration attributes: "
                        + authEvent.getConfigAttributes());
            }
        }

        if (event instanceof AuthorizationFailureEvent) {
            AuthorizationFailureEvent authEvent = (AuthorizationFailureEvent) event;

            if (log.isWarnEnabled()) {
                log.warn("Security authorization failed due to: "
                        + authEvent.getAccessDeniedException()
                        + "; authenticated principal: " + authEvent.getAuthentication()
                        + "; secure object: " + authEvent.getSource()
                        + "; configuration attributes: "
                        + authEvent.getConfigAttributes());
            }
        }

        if (event instanceof AuthorizedEvent) {
            AuthorizedEvent authEvent = (AuthorizedEvent) event;

            if (log.isInfoEnabled()) {
                log.info("Security authorized for authenticated principal: "
                        + authEvent.getAuthentication() + "; secure object: "
                        + authEvent.getSource() + "; configuration attributes: "
                        + authEvent.getConfigAttributes());
            }
        }

        if (event instanceof PublicInvocationEvent) {
            PublicInvocationEvent authEvent = (PublicInvocationEvent) event;

            if (log.isInfoEnabled()) {
                log.info("Security interception not required for public secure object: "
                        + authEvent.getSource());
            }
        }
    }
}
