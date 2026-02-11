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

    @GetMapping(produces = "application/json", value = "/crearacercamiento")
    public ResponseEntity<APIResponseDTO<Object>> getParams( @RequestParam String nit) {
        Set<ConfigParamDTO> contactResult = configParamService.getConfigParamsByParent("contactResult");
        Set<ConfigParamDTO> employeeNumber = configParamService.getConfigParamsByParent("employeeNumber");
        Set<ConfigParamDTO> positions = configParamService.getConfigParamsByParent("position");
        Set<ConfigParamDTO> numberTypes = configParamService.getConfigParamsByParent("numberType");
        //CustomerDataViewEntity customerDataView = customerService.getCustomerDataView(nit);

        Map<String, Object> response = new HashMap<>();
        response.put("contactResult", contactResult);
        response.put("employeeNumber", employeeNumber);
        response.put("positions", positions);
        response.put("numberTypes", numberTypes);
        //response.put("customerDataView", customerDataView);
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess ));
    }


}
