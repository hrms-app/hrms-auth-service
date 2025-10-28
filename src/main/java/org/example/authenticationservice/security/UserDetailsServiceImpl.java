package org.example.authenticationservice.security;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.model.User;
import org.example.authenticationservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found with username: " + username));

        // constructor UserPrincipal(User user) KHÔNG set password/authority
        // Cần dùng build()
        return UserPrincipal.build(user);
    }
}
