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

@Service
@RequiredArgsConstructor
public class EasyBankUserDetailService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole()));
    return new User(customer.getEmail(), customer.getPassword(), authorities);
  }
}
