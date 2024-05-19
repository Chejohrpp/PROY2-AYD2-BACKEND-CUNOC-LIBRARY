package com.hrp.libreriacunocbackend.service.datafile;

import com.hrp.libreriacunocbackend.dto.datafile.DataFileResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.UploadDataFileException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataFileService {
    DataFileResponseDTO handleDataFile(MultipartFile file) throws UploadDataFileException;

    List<String> verifySystemData();
}
