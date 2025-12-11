package com.expensetracker.controller;


import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;
import com.expensetracker.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense-tracker/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up")
    public ResponseEntity<UserResponseDTO> signUp(@Validated @RequestBody UserRequestDTO userDTO){

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(userDTO));

    }

}
