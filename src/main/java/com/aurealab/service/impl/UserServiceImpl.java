package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.dto.request.PasswordRequestDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.mapper.UserMapper;
import com.aurealab.model.aurea.entity.PersonEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.specs.UserSpecs;
import com.aurealab.service.UserService;
import com.aurealab.service.impl.management.ConfigParamServiceImpl;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import com.aurealab.util.exceptions.DataPersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<APIResponseDTO<String>> getUsers(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable("ok", constants.success.findedSuccess, findAllPageableUsers(pageable, searchValue, false)));

    }

    public ResponseEntity<APIResponseDTO<String>> getCompanyUsers(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable("ok", constants.success.findedSuccess, findAllPageableUsers(pageable, searchValue, true)));

    }


    public ResponseEntity<APIResponseDTO<String>> changePass(PasswordRequestDTO passwords){
        if(!Objects.equals(passwords.newPassword(), passwords.confirmPassword())){
            throw new RuntimeException(constants.messages.passwordsNotEquals);
        }

        UserEntity currentUser = getUserEntityById(jwtUtils.getCurrentUserId());

        if (!passwordEncoder.matches(passwords.oldPassword(), currentUser.getPassword())) {
            throw new BaseException(constants.messages.invalidPassword, constants.messages.invalidPassword) {};
        }

        currentUser.setPassword(passwordEncoder.encode(passwords.newPassword()));

        userRepository.save(currentUser);

        return ResponseEntity.ok(APIResponseDTO.success(constants.success.updatedSuccess, constants.success.updatedSuccess));

    }

    public Page<UserTableResponseDTO> findAllPageableUsers(Pageable pageable, String searchValue, boolean isWithCompany ) {
        Specification<UserEntity> spec = isWithCompany?
                UserSpecs.searchWithCompany(searchValue, jwtUtils.getCurrentTenant()):
                UserSpecs.search(searchValue);
        Page<UserEntity> users = userRepository.findAll(spec, pageable);
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

    public ResponseEntity<APIResponseDTO<UserDTO>> getMyAccount() {

        return ResponseEntity.ok(APIResponseDTO.success(
                getUserById(jwtUtils.getCurrentUserId()),
                constants.success.findedSuccess));
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

    public APIResponseDTO<String> updateMyUser(UserDTO user) {
        APIResponseDTO<String> response;

        try {
            updateMyAccount(user);
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

    private UserEntity updateMyAccount(UserDTO user) {
        UserEntity currentUser = getUserEntityById(jwtUtils.getCurrentUserId());
        PersonEntity currentPerson = currentUser.getPerson();

        currentPerson.setDocumentType(user.getPerson().getDocumentType());
        currentPerson.setDocumentNumber(user.getPerson().getDocumentNumber());
        currentPerson.setNames(user.getPerson().getNames());
        currentPerson.setSurnames(user.getPerson().getSurnames());
        currentPerson.setPhoneNumber(user.getPerson().getPhoneNumber());
        currentPerson.setBirthDate(user.getPerson().getBirthDate());
        currentPerson.setAddress(user.getPerson().getAddress());

        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPerson(currentPerson);
        currentUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(currentUser);
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
