package com.darthchild.aurthor.service;

import com.darthchild.aurthor.JWT.JwtUtils;
import com.darthchild.aurthor.model.Role;
import com.darthchild.aurthor.model.User;
import com.darthchild.aurthor.model.UserDTO;
import com.darthchild.aurthor.repo.RoleRepository;
import com.darthchild.aurthor.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class UserService {

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
     * Verifies the user's credentials by authenticating with the <b>AuthenticationManager</b>
     * <p>
     * It creates an unauthenticated <b>UsernamePasswordAuthenticationToken</b> using the provided
     * username and password, and attempts authentication. If successful, it issues a JWT
     * token for the user; otherwise, it returns an error message.
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
