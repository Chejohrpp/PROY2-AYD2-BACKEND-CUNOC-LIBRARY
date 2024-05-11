package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;

@Value
public class BookRequestAmountCopiesDTO {
    String isbn;
    Integer newCopies;
}
