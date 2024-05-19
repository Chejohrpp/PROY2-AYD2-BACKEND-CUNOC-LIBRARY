package com.hrp.libreriacunocbackend.entities.datafile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorEntity {
    int line;
    String type;
    String description;
}