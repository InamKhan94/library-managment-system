package com.library.management.system.repository;

import com.library.management.system.model.Book;
import com.library.management.system.model.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<BorrowedBook, UUID> {
    Optional<BorrowedBook> findByBookAndReturnDateIsNull(Book book);
    Optional<BorrowedBook> findByBookIdAndReturnDateIsNull(UUID bookId);
}