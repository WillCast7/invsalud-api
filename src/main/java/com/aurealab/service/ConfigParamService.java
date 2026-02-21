package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ConfigParamService {
    Set<ConfigParamDTO> getConfigParamsByParent(String parent);
    public ResponseEntity<APIResponseDTO<Object>> getCreatingCustomerParams();

}
