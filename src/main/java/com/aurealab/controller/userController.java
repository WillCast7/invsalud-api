package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.request.PasswordRequestDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administration")

public class userController {

    @Autowired
    private UserService userService;


    @GetMapping(produces = "application/json", value = "/users")
    ResponseEntity<APIResponseDTO<String>> getUsers(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "") String searchValue) {
        return userService.getUsers(page, size, searchValue);
    }

    @GetMapping(value = "/user/{id}" ,produces = "application/json")
    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getUser(@PathVariable Long id) {
        return userService.findUserAndParamsById(id);
    }

    @PostMapping(value = "/changepass" ,produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> changePass(@RequestBody @Valid PasswordRequestDTO passwords) {
        return userService.changePass(passwords);
    }

    @GetMapping(value = "/myaccount" ,produces = "application/json")
    public ResponseEntity<APIResponseDTO<UserDTO>> getMyAccount() {
        return userService.getMyAccount();
    }

    @PostMapping(produces = "application/json", value = "/user")
    public APIResponseDTO<String> postUsers(@RequestBody UserDTO user) {
        return userService.saveUser(user);
    }

    @PostMapping(produces = "application/json", value = "/myAccount")
    public APIResponseDTO<String> updateMyAccount(@RequestBody UserDTO user) {
        return userService.updateMyUser(user);
    }

    @PatchMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<String>> patchUsers(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "") String searchValue) {
        return userService.getUsers(page, size, searchValue);
    }
}
