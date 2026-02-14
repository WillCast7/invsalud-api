package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;

import java.util.List;

public interface UserService {
    public APIResponseDTO<List<UserDTO>> getUsers(int itemsPerPage, int activePage);
    public APIResponseDTO<UserDTO> getUserResponse(Long id);
    public UserDTO getSimplyUserById(Long id);
    public APIResponseDTO<String> saveUser(UserDTO user);
}
