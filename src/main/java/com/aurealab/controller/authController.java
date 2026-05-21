package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AuthResponse;
import com.aurealab.dto.ForgotPasswordRequestDTO;
import com.aurealab.dto.LoginRequest;
import com.aurealab.dto.ResetPasswordRequestDTO;
import com.aurealab.service.impl.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class authController {

    @Autowired
    UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    ResponseEntity<APIResponseDTO<AuthResponse>> login(@RequestBody @Valid LoginRequest userRequest){
        return this.userDetailService.loginUser(userRequest);
    }

    @PostMapping("/forgot-password")
    ResponseEntity<APIResponseDTO<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO userRequest){
        return userDetailService.forgotPassword(userRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponseDTO<String>> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        return userDetailService.resetPassword(request.token(), request.newPassword());
    }

    @PostMapping("/confirm-token")
    public ResponseEntity<APIResponseDTO<String>> confirmToken(@Valid String token) {
        return userDetailService.confirmToken(token);
    }

}
