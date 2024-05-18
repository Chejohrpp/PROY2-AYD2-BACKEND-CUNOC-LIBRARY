package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;
import javax.validation.constraints.*;

import java.time.LocalDateTime;

@Value
public class BookRequestDTO {
    @NotNull(message = "ISBN cannot be null")
    @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
    String isbn;

    @NotNull(message = "Author ID cannot be null")
    String idAuthor;

    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title;

    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be positive")
    Integer amount;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    Double price;

    @NotNull(message = "Publish date cannot be null")
    LocalDateTime datePublish;

    @NotNull(message = "Editorial ID cannot be null")
    String idEditorial;

}
