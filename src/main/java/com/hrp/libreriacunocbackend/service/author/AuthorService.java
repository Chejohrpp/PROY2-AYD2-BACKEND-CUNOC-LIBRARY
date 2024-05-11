package com.hrp.libreriacunocbackend.service.author;

import com.hrp.libreriacunocbackend.dto.author.AuthorRequestDTO;
import com.hrp.libreriacunocbackend.dto.author.AuthorResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    AuthorResponseDTO createAuthor(AuthorRequestDTO authorRequestDTO) throws NotAcceptableException;

    List<AuthorResponseDTO> getByNameFilter(String filter) throws BadRequestException;

    Optional<Author> getAuthorByName(String name);

    Optional<Author> getAuthorById(String id);
}
