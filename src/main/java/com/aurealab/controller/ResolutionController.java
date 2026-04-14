package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.service.Inventory.ResolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resolutions")
public class ResolutionController {

    @Autowired
    private ResolutionService resolutionService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getResolutionsForTable(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return resolutionService.getResolutions(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> getResolutionById(@PathVariable Long id) {
        return resolutionService.getResolutionById(id);
    }
}
