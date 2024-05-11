package com.hrp.libreriacunocbackend.service.book;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestDTO;
import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;

public interface BookService {

    BookResponseDTO createBook(BookRequestDTO bookRequestDTO) throws NotAcceptableException;

    BookResponseDTO UpdateAmountCopies(BookRequestAmountCopiesDTO bookRequestAmountCopiesDTO) throws NotAcceptableException, EntityNotFoundException;

    List<BookResponseDTO> getAll();

    List<BookResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException;

    List<BookResponseDTO> getByAttribute(BookRequestAttributeDTO bookRequestAttributeDTO) throws BadRequestException;
}
