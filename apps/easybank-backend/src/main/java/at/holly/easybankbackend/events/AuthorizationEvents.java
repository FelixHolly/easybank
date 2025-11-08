package at.holly.easybankbackend.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationEvents {

  @EventListener
  public void handleAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
    var auth = event.getAuthentication().get();
    String username = (auth != null) ? auth.getName() : "anonymous";
    log.warn("Authorization denied for user '{}' on {}", username, event.getSource());
  }

}
