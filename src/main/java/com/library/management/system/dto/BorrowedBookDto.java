package com.library.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedBookDto {
    private UUID id;
    private UUID borrowerId;
    private UUID bookId;
    private String borrowerName;
    private String borrowerEmail;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
}
