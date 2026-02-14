package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administracion/usuarios")

public class userController {

    @Autowired
    private UserService userService;


    @GetMapping(produces = "application/json")
    public APIResponseDTO<List<UserDTO>> getUsers(@RequestParam(defaultValue = "10") int itemsPerPage,
                                                  @RequestParam(defaultValue = "0") int activePage) {
        return userService.getUsers(itemsPerPage, activePage);
    }

    @GetMapping(value = "/{id}" ,produces = "application/json")
    public APIResponseDTO<UserDTO> getUser(@PathVariable Long id) {
        return userService.getUserResponse(id);
    }

    @PostMapping(produces = "application/json")
    public APIResponseDTO<String> postUsers(@Valid @RequestBody UserDTO user) {
        return userService.saveUser(user);
    }

    @PutMapping(produces = "application/json")
    public APIResponseDTO<List<UserDTO>> putUsers(@RequestParam(defaultValue = "10") int itemsPerPage,
                                                   @RequestParam(defaultValue = "0") int activePage) {
        return userService.getUsers(itemsPerPage, activePage);
    }

    @PatchMapping(produces = "application/json")
    public APIResponseDTO<List<UserDTO>> patchUsers(@RequestParam(defaultValue = "10") int itemsPerPage,
                                                  @RequestParam(defaultValue = "0") int activePage) {
        return userService.getUsers(itemsPerPage, activePage);
    }
}
