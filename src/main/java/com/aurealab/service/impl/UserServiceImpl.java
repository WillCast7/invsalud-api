package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.RoleDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.mapper.UserMapper;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.service.UserService;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${security.users.defaultpass}")
    private String defaultPass;

    public APIResponseDTO<List<UserDTO>> getUsers(int itemPerPage, int activePage) {

        APIResponseDTO<List<UserDTO>> response;
        Page<UserEntity> users;

        final Pageable pageable = PageRequest.of(activePage, itemPerPage);
        try {
            users = userRepository.findAll(pageable);
            if (users.hasContent()) {
                List<UserDTO> dtoList = new ArrayList<>();

                for (UserEntity user : users) {
                    dtoList.add(UserMapper.toDto(user));
                }
                response = APIResponseDTO.withPageable(dtoList, constants.messages.consultGood, users.getPageable());
            } else {
                response = APIResponseDTO.failure(constants.messages.noData, "vacio");
            }
        } catch (Exception e) {
            response = APIResponseDTO.failure(constants.errors.findError + " los usuarios", e.getMessage());
        }
        return response;
    }

    public APIResponseDTO<UserDTO> getUserResponse(Long id) {
        UserDTO user = getSimplyUserById(id);
        if(user.equals(null)){
            throw new RuntimeException(constants.messages.noData);
        }

        return APIResponseDTO.success(user, constants.messages.consultGood);
    }

    public APIResponseDTO<String> saveUser(UserDTO user) {
        APIResponseDTO<String> response;
        try {
            userRepository.save(UserMapper.toEntity(user));
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

    public UserDTO getSimplyUserById(Long id){
        Optional<UserEntity> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()){
            System.out.println("Esta presente");
            System.out.println(userOptional.get().getPerson().getNames());
            return com.aurealab.mapper.UserMapper.toDtoSimplyResponse(userOptional.get());
        } else {
            System.out.println("No esta presente");
            return null;
        }
    }
}
