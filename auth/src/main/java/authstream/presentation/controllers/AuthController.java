package authstream.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import authstream.application.dtos.UserDto;
import authstream.application.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto loginRequest) {
        UserDto user = userService.checkLogin(loginRequest);
        return ResponseEntity.ok(user);
    }
}