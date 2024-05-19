package com.hrp.libreriacunocbackend.controllers.datafile;

import com.hrp.libreriacunocbackend.dto.datafile.DataFileResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.UploadDataFileException;
import com.hrp.libreriacunocbackend.service.datafile.DataFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/v1/datafile")

public class DataFileController {
    private final DataFileService dataFileService;


    public DataFileController(DataFileService dataFileService) {
        this.dataFileService = dataFileService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<DataFileResponseDTO> uploadDataFile(@RequestParam("file") MultipartFile file) throws UploadDataFileException {
        return ResponseEntity.ok(dataFileService.handleDataFile(file));
    }

    @GetMapping(path = "/verifySystemData")
    public ResponseEntity<List<String>> verifySystemData() {
        return ResponseEntity.ok(dataFileService.verifySystemData());
    }
}
