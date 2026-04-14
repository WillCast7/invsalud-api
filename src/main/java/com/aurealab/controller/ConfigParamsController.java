package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.CustomerService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> getCreatingCustomerParams() {
       return configParamService.getCreatingCustomerParams();
    }

    @GetMapping(produces = "application/json", value = "/user")
    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getCreatinguserParams() {
        return configParamService.getCreatingUserParams();
    }

    @GetMapping(produces = "application/json", value = "/parent/{parent}")
    public ResponseEntity<APIResponseDTO<Set<ConfigParamDTO>>> findParamsByParent(@PathVariable String parent) {
        System.out.println("parent here");
        System.out.println(parent);
        return configParamService.searchParamsByParent(parent);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getAllConfigParams(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "") String searchValue) {
        return configParamService.getConfigParams(page, size, searchValue);
    }

}
