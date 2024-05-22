package com.hrp.libreriacunocbackend.service.author;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.hrp.libreriacunocbackend.dto.author.AuthorRequestDTO;
import com.hrp.libreriacunocbackend.dto.author.AuthorResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.books.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthorServiceImplTest {

    private AuthorRepository authorRepository;
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        authorRepository = mock(AuthorRepository.class);
        authorService = new AuthorServiceImpl(authorRepository);
    }

    @Test
    void testCreateAuthor() throws NotAcceptableException, DuplicatedEntityException {
        // Arrange
        AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO("1", "John Doe");
        Author author = new Author();
        author.setIdAuthor("1");
        author.setName("John Doe");
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        // Act
        AuthorResponseDTO response = authorService.createAuthor(authorRequestDTO);

        // Assert
        assertEquals(author.getIdAuthor(), response.getIdAuthor());
        assertEquals(author.getName(), response.getName());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testCreateAuthor_DuplicatedEntityException() throws NotAcceptableException {
        // Arrange
        AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO("1", "John Doe");
        when(authorRepository.findById("1")).thenReturn(Optional.of(new Author()));

        // Act & Assert
        assertThrows(DuplicatedEntityException.class, () -> {
            authorService.createAuthor(authorRequestDTO);
        });
    }

    @Test
    void testGetByNameFilter() throws BadRequestException {
        // Arrange
        String filter = "John";
        Author author = new Author();
        author.setName("John Doe");
        author.setIdAuthor("1");
        Author author2 = new Author();
        author2.setName("John Smith");
        author2.setIdAuthor("2");
        List<Author> authors = Arrays.asList(author, author2);
        when(authorRepository.getAuthorsByFilter(filter)).thenReturn(authors);

        // Act
        List<AuthorResponseDTO> response = authorService.getByNameFilter(filter);

        // Assert
        assertEquals(authors.size(), response.size());
        assertEquals(authors.get(0).getIdAuthor(), response.get(0).getIdAuthor());
        assertEquals(authors.get(0).getName(), response.get(0).getName());
        assertEquals(authors.get(1).getIdAuthor(), response.get(1).getIdAuthor());
        assertEquals(authors.get(1).getName(), response.get(1).getName());
    }

    @Test
    void testGetByNameFilter_EmptyResult() throws BadRequestException {
        // Arrange
        String filter = "John";
        when(authorRepository.getAuthorsByFilter(filter)).thenReturn(Collections.emptyList());

        // Act & Assert
        List<AuthorResponseDTO> response = authorService.getByNameFilter(filter);
        assertEquals(0, response.size());
    }
}
