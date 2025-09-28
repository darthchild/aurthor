package com.darthchild.aurthor.service;

import com.darthchild.aurthor.model.Role;
import com.darthchild.aurthor.model.User;
import com.darthchild.aurthor.model.UserDTO;
import com.darthchild.aurthor.repo.RoleRepository;
import com.darthchild.aurthor.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public Boolean addUser(UserDTO dto){
        User savedUser = userRepository.save(User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
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

}
