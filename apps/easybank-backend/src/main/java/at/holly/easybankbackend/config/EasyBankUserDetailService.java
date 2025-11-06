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

import java.util.ArrayList;
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

    // Combine both roles and authorities into a single list
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    // Add roles (prefixed with "ROLE_")
    List<GrantedAuthority> roleAuthorities = customer.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
        .collect(Collectors.toList());
    grantedAuthorities.addAll(roleAuthorities);

    // Add specific authorities (no prefix)
    List<GrantedAuthority> specificAuthorities = customer.getAuthorities().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        .collect(Collectors.toList());
    grantedAuthorities.addAll(specificAuthorities);

    return new User(customer.getEmail(), customer.getPassword(), grantedAuthorities);
  }
}
