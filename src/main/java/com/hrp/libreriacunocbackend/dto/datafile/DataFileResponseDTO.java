package com.hrp.libreriacunocbackend.dto.datafile;

import com.hrp.libreriacunocbackend.entities.datafile.ErrorEntity;
import lombok.Value;

import java.util.List;

@Value
public class DataFileResponseDTO {
    int records;
    List<ErrorEntity> errors;

}
