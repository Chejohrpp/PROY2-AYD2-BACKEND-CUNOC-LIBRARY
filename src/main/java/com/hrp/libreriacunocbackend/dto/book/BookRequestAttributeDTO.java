package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;

@Value
public class BookRequestAttributeDTO {
    String attributeName;
    String filter;
}
