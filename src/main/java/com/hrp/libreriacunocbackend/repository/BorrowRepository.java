package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
}
