package com.hrp.libreriacunocbackend.repository.specifications.user;

import com.hrp.libreriacunocbackend.entities.user.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class StudentSpecification {

    public static Specification<Student> likeAttributes(List<String> attributes, String filter) {
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + filter.toLowerCase() + "%";
            Predicate[] predicates = attributes.stream()
                    .map(attr -> criteriaBuilder.like(criteriaBuilder.lower(root.get(attr)), pattern))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        };
    }
}
