package com.darthchild.aurthor.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Essentially a wrapper around the application's <b>User</b> entity
 * that adapts it to conform to Spring Security's <b>UserDetails</b> contract
 * so it can integrate into its authentication and authorization mechanisms
 */
public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Converts the User's roles into a collection of <b>GrantedAuthority</b> objects
     * so that Spring Security can enforce role-based access.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
