package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.BatchDTO;
import com.aurealab.service.Inventory.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    BatchService batchService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getBatches(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "") String searchValue) {
        return batchService.findPaginatedBatches(page, size, searchValue);
    }

    @PutMapping(produces = "application/json", value = "/changestatus/{id}")
    public ResponseEntity<APIResponseDTO<BatchDTO>> changeStatus(@PathVariable Long id) {
        return batchService.changeStatus(id);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<BatchDTO>> SaveBatch(@RequestBody BatchDTO batch) {
        return batchService.saveBatch(batch);
    }
}
