package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;
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

    @PostMapping(produces = "application/json", value = "/user")
    public APIResponseDTO<String> postUsers(@RequestBody UserDTO user) {
        System.out.print("usersada");
        System.out.print(user);
        return userService.saveUser(user);
    }

    @PutMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<String>> putUsers(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "") String searchValue) {
        return userService.getUsers(page, size, searchValue);
    }

    @PatchMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<String>> patchUsers(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "") String searchValue) {
        return userService.getUsers(page, size, searchValue);
    }
}
