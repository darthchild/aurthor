package com.darthchild.aurthor.service;

import com.darthchild.aurthor.model.UserPrincipal;
import com.darthchild.aurthor.repo.UserRepository;
import com.darthchild.aurthor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class authorUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(user);
    }

    public boolean userExists(String username) {
        try {
            return userRepository.findByUsername(username).isPresent();
        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }
}
