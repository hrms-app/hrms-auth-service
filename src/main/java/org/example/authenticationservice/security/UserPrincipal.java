package org.example.authenticationservice.security;

import lombok.*;
import org.example.authenticationservice.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private User user;

    /**
     * ✅ Static method dùng để build UserPrincipal từ entity User
     * => Giúp AuthService gọi UserPrincipal.build(user)
     */
    public static UserPrincipal build(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        user.getRoles().stream()
                                .map(role -> (GrantedAuthority) role::getName)
                                .collect(Collectors.toList())
                )
                .user(user)
                .build();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public UserPrincipal(User user) {
        this.user = user;
    }
}
