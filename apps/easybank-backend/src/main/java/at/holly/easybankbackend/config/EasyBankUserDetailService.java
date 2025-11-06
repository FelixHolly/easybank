package at.holly.easybankbackend.config;

import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EasyBankUserDetailService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Convert Set<Role> to List<GrantedAuthority>
    // Each role is prefixed with "ROLE_" as per Spring Security convention
    List<GrantedAuthority> authorities = customer.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
        .collect(Collectors.toList());

    return new User(customer.getEmail(), customer.getPassword(), authorities);
  }
}
