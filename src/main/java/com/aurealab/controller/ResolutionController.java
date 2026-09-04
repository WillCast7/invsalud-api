package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.service.Inventory.ResolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/resolutions", "/resolution"})
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

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> saveResolution(@RequestBody ResolutionDTO resolutionDTO) {
        return resolutionService.saveResolution(resolutionDTO);
    }

    @PutMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> updateResolution(@PathVariable Long id, @RequestBody ResolutionDTO resolutionDTO) {
        return resolutionService.updateResolution(id, resolutionDTO);
    }

    @PutMapping(produces = "application/json", value = "/changestatus/{id}")
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> changeStatus(@PathVariable Long id) {
        return resolutionService.changeStatus(id);
    }
}
