package at.holly.easybankbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**ø
 * This class is used to authenticate the user
 * It is only active if the profile is not prod
 */

@Component
@Profile("!prod")
@RequiredArgsConstructor
public class EasyBankAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String name = authentication.getName();
    String password = authentication.getCredentials().toString();
    UserDetails userDetails = userDetailsService.loadUserByUsername(name);
    // no password check!
    return new UsernamePasswordAuthenticationToken(name, password, userDetails.getAuthorities());

  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

}
