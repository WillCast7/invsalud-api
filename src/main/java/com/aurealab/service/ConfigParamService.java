package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ConfigParamService {
    public Set<ConfigParamDTO> findParamsByParent(String parent);
    public ResponseEntity<APIResponseDTO<Set<ConfigParamDTO>>> searchParamsByParent(String parent);
    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> getCreatingCustomerParams();
    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getCreatingUserParams();
    public ThirdPartyWithParamsResponseDTO findCreatingThirdPartyParams();
    public UserWithParamsResponseDTO findCreatingUserParams();
    public ResponseEntity<APIResponseDTO<String>> getConfigParams(int page, int size, String searchValue);


}
