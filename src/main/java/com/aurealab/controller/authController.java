package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AuthResponse;
import com.aurealab.dto.LoginRequest;
import com.aurealab.service.impl.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/login")
public class authController {

    @Autowired
    UserDetailServiceImpl userDetailService;

    @PostMapping
    ResponseEntity<APIResponseDTO<AuthResponse>> login(@RequestBody @Valid LoginRequest userRequest){
        return this.userDetailService.loginUser(userRequest);
    }

}
