package com.darthchild.aurthor.security;

import com.darthchild.aurthor.security.model.UserDTO;
import com.darthchild.aurthor.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class AuthController {

    @Autowired
    private AuthService userService;

    @GetMapping("/admin")
    public String adminTest(){
        return "Hello admin!";
    }

    /**
     * @return JWT token string if authenticated, else error message
     */
    @PostMapping("/login")
    public String loginUser(@RequestBody UserDTO userDTO){
        return userService.verifyUser(userDTO);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO){
        if(userService.register(userDTO))
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User created successfully!");
        else return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("User creation failed, check logs");
    }


}
