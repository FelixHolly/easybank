package at.holly.easybankbackend.config;

import at.holly.easybankbackend.model.Authority;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsService implementation that supports RBAC (Role-Based Access Control)
 *
 * Authorization Model:
 * 1. Roles → Implicit Authorities (computed from AuthorityService)
 * 2. Custom Authorities (stored in database as exceptions/additions)
 * 3. Final Authorities = Role-based + Custom (deduplicated)
 *
 * This approach:
 * - Reduces database storage (no need to store default authorities)
 * - Centralizes authority management (AuthorityService)
 * - Allows per-user customization (custom authorities table)
 * - Follows RBAC best practices
 */
@Service
@RequiredArgsConstructor
public class EasyBankUserDetailService implements UserDetailsService {

  private final CustomerRepository customerRepository;
  private final AuthorityService authorityService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Combine roles, role-based authorities, and custom authorities
    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

    // 1. Add roles (prefixed with "ROLE_")
    customer.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
        .forEach(grantedAuthorities::add);

    // 2. Add role-based authorities (computed from roles)
    Set<Authority> roleBasedAuthorities = authorityService.getDefaultAuthoritiesForRoles(customer.getRoles());
    roleBasedAuthorities.stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        .forEach(grantedAuthorities::add);

    // 3. Add custom authorities from database (exceptions/additions beyond role defaults)
    customer.getAuthorities().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        .forEach(grantedAuthorities::add);

    // Convert Set to List for Spring Security
    List<GrantedAuthority> authoritiesList = grantedAuthorities.stream().collect(Collectors.toList());

    return new User(customer.getEmail(), customer.getPassword(), authoritiesList);
  }
}
