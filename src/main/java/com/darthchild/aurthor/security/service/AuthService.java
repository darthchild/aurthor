package com.darthchild.aurthor.security.service;

import com.darthchild.aurthor.security.JWT.JwtUtils;
import com.darthchild.aurthor.security.model.Role;
import com.darthchild.aurthor.security.model.User;
import com.darthchild.aurthor.security.model.UserDTO;
import com.darthchild.aurthor.security.repo.RoleRepository;
import com.darthchild.aurthor.security.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Converts client's incoming data which is in UserDTO form to
     * User and Role entities, then saves them in the database
     */
    public Boolean register(UserDTO dto){
        User savedUser = userRepository.save(User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(dto.getEnabled())
                .build()
        );

        Set<String> roleSet = dto.getRoles();
        for(String role : roleSet){
            roleRepository.save(Role.builder()
                    .role(role)
                    .user(savedUser)
                    .build()
            );
        }
        return savedUser.getId() != null;
    }

    /**
     * Verifies the user's credentials by authenticating with the {@link AuthenticationManager}
     * <p>
     * It creates an unauthenticated <b>Authentication</b> object (using <b>UsernamePasswordAuthenticationToken</b>)
     * with the provided username & password, passes it to the AuthManager. Then attempts
     * authentication, if successful it issues a JWT token for the user; otherwise returns an
     * error message.
     */
    public String verifyUser(UserDTO dto){
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        if(authentication.isAuthenticated())
            return jwtUtils.generateToken(dto.getUsername(),new HashMap<>());
        else
            return "Couldn't authenticate user!";
    }

}
