package com.hrp.libreriacunocbackend.service.book;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestDTO;
import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookResponseDTO createBook(BookRequestDTO bookRequestDTO) throws NotAcceptableException, EntityNotFoundException, DuplicatedEntityException;

    BookResponseDTO UpdateAmountCopies(BookRequestAmountCopiesDTO bookRequestAmountCopiesDTO) throws NotAcceptableException, EntityNotFoundException;

    List<BookResponseDTO> getAll();

    List<BookResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException;

    List<BookResponseDTO> getByAttribute(BookRequestAttributeDTO bookRequestAttributeDTO) throws BadRequestException;

    Optional<Book> getByIsbn(String isbn);

    Optional<Book> getById(Long id);

    BookResponseDTO getByIdResponse(Long id) throws EntityNotFoundException;

    List<BookResponseDTO> getBooksOutOfStock();

    List<BookResponseDTO> getBooksNeverBorrowed();

    long count();
}
