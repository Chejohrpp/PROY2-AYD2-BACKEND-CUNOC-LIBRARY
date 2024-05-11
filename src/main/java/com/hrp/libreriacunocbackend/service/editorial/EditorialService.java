package com.hrp.libreriacunocbackend.service.editorial;

import com.hrp.libreriacunocbackend.dto.editorial.EditorialRequestDTO;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Editorial;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;
import java.util.Optional;

public interface EditorialService {

    EditorialResponseDTO create(EditorialRequestDTO editorialRequestDTO) throws NotAcceptableException;

    Optional<Editorial> getEditorialById(String id);

    Optional<Editorial> getEditorialByName(String name);

    List<EditorialResponseDTO> getByFilter(String filter) throws BadRequestException;
}
