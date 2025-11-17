package com.emmanuel.user_service.security;

import com.emmanuel.user_service.repository.UserRepository;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isAccountNonExpired(),
        user.isCredentialsNonExpired(),
        user.isAccountNonLocked(),
        Stream.concat(
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())),
                user.getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.name())))
            .toList());
  }
}
