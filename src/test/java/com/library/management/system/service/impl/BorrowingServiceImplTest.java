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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowingServiceImplTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookService bookService;

    @Mock
    private BorrowerService borrowerService;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    private UUID borrowerId;
    private UUID bookId;
    private BorrowerDto borrowerDto;
    private BookDto bookDto;
    private Borrower borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        borrowerId = UUID.randomUUID();
        bookId = UUID.randomUUID();

        borrowerDto = new BorrowerDto(borrowerId, "Inam", "ullahkhaninam@gmail.com");
        bookDto = new BookDto(bookId, "978-3-16-148410-0", "Java Fundamentals", "John Smith");

        borrower = new Borrower();
        borrower.setId(borrowerId);
        borrower.setName(borrowerDto.getName());
        borrower.setEmail(borrowerDto.getEmail());

        book = new Book();
        book.setId(bookId);
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
    }

    @Test
    void borrowBook_Success() {
        // Create a non-null BorrowedBook for the save() mock
        BorrowedBook savedBorrowedBook = BorrowedBook.builder()
                .id(UUID.randomUUID())
                .borrower(borrower)
                .book(book)
                .borrowDate(LocalDateTime.now())
                .build();

        when(borrowerService.findBorrowerById(borrowerId)).thenReturn(borrower);
        when(bookService.findBookById(bookId)).thenReturn(book);
        when(bookService.mapToEntity(bookDto)).thenReturn(book);
        when(borrowerService.mapToEntity(borrowerDto)).thenReturn(borrower);
        when(borrowingRepository.findByBookAndReturnDateIsNull(book)).thenReturn(Optional.empty());
        when(borrowingRepository.save(any(BorrowedBook.class))).thenReturn(savedBorrowedBook);

        BorrowedBookDto borrowedBookDto = borrowingService.borrowBook(borrowerId, bookId);

        assertEquals(borrowerId, borrowedBookDto.getBorrowerId());
        assertEquals(bookId, borrowedBookDto.getBookId());
        assertEquals(bookDto.getTitle(), borrowedBookDto.getBookTitle());
        assertEquals(borrowerDto.getName(), borrowedBookDto.getBorrowerName());

        verify(borrowingRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    void borrowBook_BookAlreadyBorrowed_ThrowsException() {
        when(borrowerService.findBorrowerById(borrowerId)).thenReturn(borrower);
        when(bookService.findBookById(bookId)).thenReturn(book);
        when(bookService.mapToEntity(bookDto)).thenReturn(book);
        when(borrowingRepository.findByBookAndReturnDateIsNull(book)).thenReturn(Optional.of(new BorrowedBook()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                borrowingService.borrowBook(borrowerId, bookId));

        assertEquals("Book is already borrowed", exception.getMessage());
    }

    @Test
    void returnBook_Success() {
        BorrowedBook borrowedBook = BorrowedBook.builder()
                .id(UUID.randomUUID())
                .borrower(borrower)
                .book(book)
                .borrowDate(LocalDateTime.now())
                .build();

        when(borrowerService.findBorrowerById(borrowerId)).thenReturn(borrower);
        when(bookService.findBookById(bookId)).thenReturn(book);
        when(bookService.mapToEntity(bookDto)).thenReturn(book);
        when(borrowerService.mapToEntity(borrowerDto)).thenReturn(borrower);
        when(borrowingRepository.findByBookAndReturnDateIsNull(book)).thenReturn(Optional.of(borrowedBook));
        when(borrowingRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);

        BorrowedBookDto returnedBookDto = borrowingService.returnBook(borrowerId, bookId);

        assertEquals(borrowerId, returnedBookDto.getBorrowerId());
        assertEquals(bookId, returnedBookDto.getBookId());

        verify(borrowingRepository, times(1)).save(borrowedBook);
        assertNotNull(borrowedBook.getReturnDate());
    }

    @Test
    void returnBook_BookNotBorrowed_ThrowsException() {
        when(borrowerService.findBorrowerById(borrowerId)).thenReturn(borrower);
        when(bookService.findBookById(bookId)).thenReturn(book);
        when(bookService.mapToEntity(bookDto)).thenReturn(book);
        when(borrowingRepository.findByBookAndReturnDateIsNull(book)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                borrowingService.returnBook(borrowerId, bookId));

        assertEquals("Book is not borrowed", exception.getMessage());
    }

    @Test
    void returnBook_BookBorrowedByAnotherUser_ThrowsException() {
        Borrower anotherBorrower = new Borrower();
        anotherBorrower.setId(UUID.randomUUID());

        BorrowedBook borrowedBook = BorrowedBook.builder()
                .borrower(anotherBorrower)
                .book(book)
                .borrowDate(LocalDateTime.now())
                .build();

        when(borrowerService.findBorrowerById(borrowerId)).thenReturn(borrower);
        when(bookService.findBookById(bookId)).thenReturn(book);
        when(bookService.mapToEntity(bookDto)).thenReturn(book);
        when(borrowerService.mapToEntity(borrowerDto)).thenReturn(borrower);
        when(borrowingRepository.findByBookAndReturnDateIsNull(book)).thenReturn(Optional.of(borrowedBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                borrowingService.returnBook(borrowerId, bookId));

        assertEquals("This borrower did not borrow this book", exception.getMessage());
    }
}
