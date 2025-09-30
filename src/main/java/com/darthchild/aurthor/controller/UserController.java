package com.darthchild.aurthor.controller;

import com.darthchild.aurthor.model.UserDTO;
import com.darthchild.aurthor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String adminTest(){
        return "Hello admin!";
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

    @PostMapping("/login")
    public String loginUser(@RequestBody UserDTO userDTO){
        return userService.verifyUser(userDTO);
    }

}
