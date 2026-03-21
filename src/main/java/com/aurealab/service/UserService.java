package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.request.PasswordRequestDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.model.aurea.entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    public ResponseEntity<APIResponseDTO<String>> getUsers(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<String>> getCompanyUsers(int page, int size, String searchValue);
    public APIResponseDTO<UserDTO> getUserResponse(Long id);
    public UserTableResponseDTO getSimplyUserById(Long id);
    public APIResponseDTO<String> saveUser(UserDTO user);
    public UserDTO getUserById(Long id);
    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> findUserAndParamsById(Long id);
    public UserEntity getUserEntityById(Long id);
    public ResponseEntity<APIResponseDTO<UserDTO>> getMyAccount();
    public ResponseEntity<APIResponseDTO<String>> changePass(PasswordRequestDTO passwords);
    public APIResponseDTO<String> updateMyUser(UserDTO user);
}
