package at.holly.easybankbackend.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {

  @EventListener
  public void onSuccess (AuthenticationSuccessEvent event) {
    log.info("Authentication successful for user: {}", event.getAuthentication().getName());
  }

  @EventListener
  public void onFailure (AbstractAuthenticationFailureEvent event) {
    log.error("Authentication failed for user: {}", event.getAuthentication().getName());
  }

}
