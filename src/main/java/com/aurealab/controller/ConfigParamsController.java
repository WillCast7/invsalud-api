package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.CustomerService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/configparams")
public class ConfigParamsController {

    @Autowired
    ConfigParamService configParamService;


    @Autowired
    CustomerService customerService;

        @GetMapping(produces = "application/json", value = "/thirdParty")
    public ResponseEntity<APIResponseDTO<Object>> getCreatingCustomerParams() {
       return configParamService.getCreatingCustomerParams();
    }


}
