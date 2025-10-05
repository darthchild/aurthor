package com.darthchild.aurthor.security.model;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Boolean enabled;
    private Set<String> roles;
}
