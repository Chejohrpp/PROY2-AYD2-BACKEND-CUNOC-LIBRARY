package com.hrp.libreriacunocbackend.repository.specifications.books;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAttributeDTO;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> likeAttributes(BookRequestAttributeDTO bookRequestAttributeDTO){
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + bookRequestAttributeDTO.getFilter().toLowerCase() + "%";
            Predicate[] predicates = bookRequestAttributeDTO.getAttributesName().stream()
                    .map(attr -> criteriaBuilder.like(criteriaBuilder.lower(root.get(attr)), pattern))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        };
    }
}
