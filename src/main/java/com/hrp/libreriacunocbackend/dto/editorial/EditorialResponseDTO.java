package com.hrp.libreriacunocbackend.dto.editorial;

import com.hrp.libreriacunocbackend.entities.book.Editorial;
import lombok.Value;

@Value
public class EditorialResponseDTO {
    String idEditorial;
    String name;

    public EditorialResponseDTO(Editorial editorial){
        this.idEditorial = editorial.getIdEditorial();
        this.name = editorial.getName();
    }
}
