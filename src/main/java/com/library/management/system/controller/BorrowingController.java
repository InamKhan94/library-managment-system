package com.library.management.system.controller;

import com.library.management.system.dto.BorrowedBookDto;
import com.library.management.system.model.Book;
import com.library.management.system.model.BorrowedBook;
import com.library.management.system.model.Borrower;
import com.library.management.system.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;


    @PostMapping("borrower/{borrowerId}/{bookId}/borrow")
    public ResponseEntity<BorrowedBookDto> borrowBook(@PathVariable UUID borrowerId, @PathVariable UUID bookId) {
        return ResponseEntity.ok(borrowingService.borrowBook(borrowerId, bookId));
    }

    @PostMapping("borrower/{borrowerId}/{bookId}/return")
    public ResponseEntity<BorrowedBookDto> returnBook(@PathVariable UUID borrowerId, @PathVariable UUID bookId) {
        return ResponseEntity.ok(borrowingService.returnBook(borrowerId, bookId));
    }
}
