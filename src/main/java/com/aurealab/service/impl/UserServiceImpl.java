package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.RoleDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.mapper.CompanyMapper;
import com.aurealab.mapper.UserMapper;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import com.aurealab.util.exceptions.DataPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ConfigParamServiceImpl configParamsService;

    public ResponseEntity<APIResponseDTO<String>> getUsers(int page, int size, String searchValue) {


        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable("ok", constants.success.findedSuccess, findAllPageableUsers(pageable)));

    }

    public Page<UserTableResponseDTO> findAllPageableUsers(Pageable pageable){
        Page<UserEntity> users = userRepository.findAll(pageable);
        return users.map(UserMapper::toDtoSimplyResponse);
    }

    public APIResponseDTO<UserDTO> getUserResponse(Long id) {
        return APIResponseDTO.success(getUserById(id), constants.messages.consultGood);
    }

    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> findUserAndParamsById(Long id) {

        @SuppressWarnings("unchecked")
        UserWithParamsResponseDTO params = configParamsService.findCreatingUserParams();

        return ResponseEntity.ok(APIResponseDTO.success(
                UserWithParamsResponseDTO.builder()
                        .user(getUserById(id))
                        .documentTypes(params.documentTypes())
                        .roles(params.roles())
                        .build(), constants.success.findedSuccess));
    }

    public APIResponseDTO<String> saveUser(UserDTO user) {
        APIResponseDTO<String> response;
        try {
            UserEntity userEntity = UserMapper.toEntity(user);
            userEntity.setCompany(Objects.equals(user.getCompany(), null) ?
                    getUserEntityById(jwtUtils.getCurrentUserId()).getCompany(): userEntity.getCompany());
            userEntity.setCreatedBy(jwtUtils.getCurrentUserId());

            userRepository.save(userEntity);

            response = APIResponseDTO.success(constants.success.savedSuccess, constants.success.savedSuccess);

        } catch (DataIntegrityViolationException e) {
            String exceptionMessage = "Ya existe un usuario con ese ";

            if (e.getMessage().contains("users_email_key")) {
                throw new DataPersistenceException("Correo electrónico ya registrado.");
            } else if (e.getMessage().contains("users_username_key")) {
                throw new DataPersistenceException("Nombre de usuario ya registrado.");
            } else if (e.getMessage().contains("persons_phone_key")) {
                throw new DataPersistenceException("Número telefónico ya registrado.");
            } else {
                throw new DataPersistenceException("Datos duplicados.");
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            throw new BaseException(constants.errors.saveError, constants.errors.internalServerError, e) {
            };
        }

        return response;
    }

    public UserTableResponseDTO getSimplyUserById(Long id){
        Optional<UserEntity> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()){
            return UserMapper.toDtoSimplyResponse(userOptional.get());
        } else {
            System.out.println("No esta presente");
            return null;
        }
    }


    public UserDTO getUserById(Long id){
        return UserMapper.toDto(getUserEntityById(id));
    }

    public UserEntity getUserEntityById(Long id){
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()){
            return userOptional.get();
        } else {
            throw new RuntimeException(constants.messages.noData);
        }
    }
}
