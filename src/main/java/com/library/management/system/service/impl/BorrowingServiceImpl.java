package com.library.management.system.service.impl;

import com.library.management.system.dto.BookDto;
import com.library.management.system.dto.BorrowedBookDto;
import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.model.Book;
import com.library.management.system.model.BorrowedBook;
import com.library.management.system.model.Borrower;
import com.library.management.system.repository.BorrowingRepository;
import com.library.management.system.service.BookService;
import com.library.management.system.service.BorrowerService;
import com.library.management.system.service.BorrowingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookService bookService;
    private final BorrowerService borrowerService;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookService bookService, BorrowerService borrowerService) {
        this.borrowingRepository = borrowingRepository;
        this.bookService = bookService;
        this.borrowerService = borrowerService;
    }

    @Transactional
    public BorrowedBookDto borrowBook(UUID borrowerId, UUID bookId) {
        Borrower borrower = borrowerService.findBorrowerById(borrowerId);
        Book book = bookService.findBookById(bookId);

        if (borrowingRepository.findByBookAndReturnDateIsNull(book).isPresent()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        BorrowedBook borrowed = BorrowedBook.builder()
                                            .book(book)
                                            .borrower(borrower)
                                            .borrowDate(LocalDateTime.now())
                                            .build();

        return mapToDto(borrowingRepository.save(borrowed));
    }

    @Transactional
    public BorrowedBookDto returnBook(UUID borrowerId, UUID bookId) {
        Borrower borrower = borrowerService.findBorrowerById(borrowerId);
        Book book = bookService.findBookById(bookId);

        BorrowedBook borrowed = borrowingRepository.findByBookAndReturnDateIsNull(book)
                .orElseThrow(() -> new IllegalStateException("Book is not borrowed"));

        if (!borrowed.getBorrower().getId().equals(borrower.getId())) {
            throw new IllegalStateException("This borrower did not borrow this book");
        }
        borrowed.setReturnDate(LocalDateTime.now());
        return mapToDto(borrowingRepository.save(borrowed));
    }

    private BorrowedBookDto mapToDto(BorrowedBook borrowedBook) {
        return new BorrowedBookDto(
                borrowedBook.getId(),
                borrowedBook.getBorrower().getId(),
                borrowedBook.getBook().getId(),
                borrowedBook.getBorrower().getName(),
                borrowedBook.getBorrower().getEmail(),
                borrowedBook.getBook().getTitle(),
                borrowedBook.getBook().getAuthor(),
                borrowedBook.getBook().getIsbn(),
                borrowedBook.getBorrowDate(),
                borrowedBook.getReturnDate()
        );
    }
}
