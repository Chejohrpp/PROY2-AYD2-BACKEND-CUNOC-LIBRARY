package com.hrp.libreriacunocbackend.dto.student;

import lombok.Value;

import java.util.List;

@Value
public class StudentRequestAttributeDTO {
    List<String> attributesName;
    String filter;
}
