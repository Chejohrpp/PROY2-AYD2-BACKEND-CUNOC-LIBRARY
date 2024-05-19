package com.hrp.libreriacunocbackend.service.author;

import com.hrp.libreriacunocbackend.dto.author.AuthorRequestDTO;
import com.hrp.libreriacunocbackend.dto.author.AuthorResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.books.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;


    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorResponseDTO createAuthor(AuthorRequestDTO authorRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        validateAuthorRequest(authorRequestDTO);
        Author author = new Author();
        author.setIdAuthor(authorRequestDTO.getIdAuthor());
        author.setName(authorRequestDTO.getName());
        author = authorRepository.save(author);
        return new AuthorResponseDTO(author);
    }

    @Override
    public List<AuthorResponseDTO> getByNameFilter(String filter) throws BadRequestException {
        return authorRepository.getAuthorsByFilter(filter)
                .stream()
                .map(AuthorResponseDTO :: new)
                .collect(Collectors.toList());
    }

    private void validateAuthorRequest(AuthorRequestDTO authorRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        if (authorRequestDTO.getIdAuthor() == null || authorRequestDTO.getIdAuthor().isEmpty()) {
            throw new NotAcceptableException("the id cannot be null or empty");
        }
        if (authorRequestDTO.getName() == null || authorRequestDTO.getName().isEmpty()){
            throw new NotAcceptableException("Name cannot be null or empty");
        }
        if (getAuthorByName(authorRequestDTO.getName()).isPresent()){
            throw new DuplicatedEntityException("Name is already present");
        }
        if (getAuthorById(authorRequestDTO.getIdAuthor()).isPresent()){
            throw new DuplicatedEntityException("id already present");
        }
    }

    @Override
    public Optional<Author> getAuthorByName(String name){
        return authorRepository.findByName(name);
    }

    @Override
    public Optional<Author> getAuthorById(String id) {
        return authorRepository.findById(id);
    }
}
