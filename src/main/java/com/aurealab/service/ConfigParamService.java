package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ConfigParamService {
    Set<ConfigParamDTO> getConfigParamsByParent(String parent);
    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> getCreatingCustomerParams();
    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getCreatingUserParams();
    public ThirdPartyWithParamsResponseDTO findCreatingThirdPartyParams();
    public UserWithParamsResponseDTO findCreatingUserParams();


}
