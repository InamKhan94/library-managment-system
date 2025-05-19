package com.library.management.system.service;

import com.library.management.system.dto.BorrowedBookDto;
import com.library.management.system.model.BorrowedBook;

import java.util.UUID;

public interface BorrowingService {

    BorrowedBookDto borrowBook(UUID borrowerId, UUID bookId);
    BorrowedBookDto returnBook(UUID borrowerId, UUID bookId);

    }
