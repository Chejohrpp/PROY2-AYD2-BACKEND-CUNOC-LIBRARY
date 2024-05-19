package com.hrp.libreriacunocbackend.service.book;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestDTO;
import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.book.Editorial;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.books.BookRepository;
import com.hrp.libreriacunocbackend.repository.specifications.books.BookSpecification;
import com.hrp.libreriacunocbackend.service.author.AuthorService;
import com.hrp.libreriacunocbackend.service.editorial.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final EditorialService editorialService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService, EditorialService editorialService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.editorialService = editorialService;
    }

    @Override
    public BookResponseDTO createBook(@Valid BookRequestDTO bookRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        validateBookRequest(bookRequestDTO);

        Author author = authorService.getAuthorById(bookRequestDTO.getIdAuthor())
                .orElseThrow(() -> new EntityNotFoundException(String.format("author %s not found",bookRequestDTO.getIdAuthor())));

        Editorial editorial = editorialService.getEditorialById(bookRequestDTO.getIdEditorial())
                .orElseThrow(() -> new EntityNotFoundException(String.format("editorial %s not found", bookRequestDTO.getIdEditorial())));

        Book newbook = new Book();
        newbook.setIsbn(bookRequestDTO.getIsbn());
        newbook.setTitle(bookRequestDTO.getTitle());
        newbook.setAmount(bookRequestDTO.getAmount());
        newbook.setPrice(bookRequestDTO.getPrice());
        newbook.setDatePublish(LocalDate.from(bookRequestDTO.getDatePublish()));
        newbook.setAuthor(author);
        newbook.setEditorial(editorial);

        newbook = bookRepository.save(newbook);

        return new BookResponseDTO(newbook);

    }

    @Override
    public BookResponseDTO UpdateAmountCopies(BookRequestAmountCopiesDTO bookRequestAmountCopiesDTO) throws NotAcceptableException, EntityNotFoundException {
        Book book = bookRepository.findByIsbn(bookRequestAmountCopiesDTO.getIsbn())
                .orElseThrow(() -> new EntityNotFoundException(String.format("book with the isbn %s not found", bookRequestAmountCopiesDTO.getIsbn())));
        //validate if is below to zero
        if (bookRequestAmountCopiesDTO.getNewCopies() < 0 && Math.abs(bookRequestAmountCopiesDTO.getNewCopies()) > book.getAmount()  ) {
            throw new NotAcceptableException("the new amount of copies cant be below to zero");
        }
        book.setAmount(book.getAmount() + bookRequestAmountCopiesDTO.getNewCopies());
        book =  bookRepository.save(book);
        return new BookResponseDTO(book);
    }

    @Override
    public List<BookResponseDTO> getAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException {
        return bookRepository.findByIndexRange(startIndex, endIndex)
                .stream()
                .map(BookResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getByAttribute(BookRequestAttributeDTO bookRequestAttributeDTO) throws BadRequestException {
        return bookRepository.findAll(BookSpecification.likeAttributes(bookRequestAttributeDTO))
                .stream()
                .map(BookResponseDTO::new)
                .collect(Collectors.toList());

    }

    @Override
    public Optional<Book> getByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Optional<Book> getById(Long id){
        return bookRepository.findById(id);
    }

    private void validateBookRequest(BookRequestDTO bookRequestDTO) throws NotAcceptableException {
        if (bookRequestDTO.getIsbn() == null || bookRequestDTO.getIsbn().isBlank() || bookRequestDTO.getIsbn().length() < 7 || bookRequestDTO.getIsbn().length() > 13) {
            throw new NotAcceptableException("ISBN cannot be null, empty, and must be between 7 and 13 characters");
        }
        if (bookRequestDTO.getIdEditorial() == null || bookRequestDTO.getIdEditorial().isBlank()) {
            throw new NotAcceptableException("Editorial ID cannot be null or empty");
        }
        if (bookRequestDTO.getTitle() == null || bookRequestDTO.getTitle().isBlank() || bookRequestDTO.getTitle().length() > 255) {
            throw new NotAcceptableException("Title cannot be null, empty, and must be between 1 and 255 characters");
        }
        if (bookRequestDTO.getIdAuthor() == null || bookRequestDTO.getIdAuthor().isBlank()) {
            throw new NotAcceptableException("Author ID cannot be null or empty");
        }
        if (bookRequestDTO.getDatePublish() == null) {
            throw new NotAcceptableException("Publish date cannot be null");
        }
        if (bookRequestDTO.getAmount() == null || bookRequestDTO.getAmount() <= 0) {
            throw new NotAcceptableException("Amount must be positive");
        }
        if (bookRequestDTO.getPrice() == null || bookRequestDTO.getPrice() <= 0) {
            throw new NotAcceptableException("Price must be positive");
        }
        Optional<Book> book = bookRepository.findByIsbn(bookRequestDTO.getIsbn());
        if (book.isPresent()){
            throw new NotAcceptableException("the isbn already present");
        }

    }
}
