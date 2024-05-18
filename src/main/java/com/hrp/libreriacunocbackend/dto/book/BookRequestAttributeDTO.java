package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;

import java.util.List;

@Value
public class BookRequestAttributeDTO {
    List<String> attributesName;
    String filter;
}
