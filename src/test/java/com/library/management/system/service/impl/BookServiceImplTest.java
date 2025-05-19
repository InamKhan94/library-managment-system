package com.library.management.system.service.impl;

import com.library.management.system.dto.BookDto;
import com.library.management.system.exception.ResourceNotFoundException;
import com.library.management.system.model.Book;
import com.library.management.system.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterBook() {
        // Given
        BookDto bookDto = BookDto.builder()
                .isbn("978-3-16-148410-0")
                .title("Test Book")
                .author("Inam")
                .build();

        Book savedBook = new Book(UUID.randomUUID(), "978-3-16-148410-0", "Test Book", "Inam", 1L);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // When
        BookDto registeredBook = bookService.registerBook(bookDto);

        // Then
        assertNotNull(registeredBook.getId());
        assertEquals(bookDto.getIsbn(), registeredBook.getIsbn());
        assertEquals(bookDto.getTitle(), registeredBook.getTitle());
        assertEquals(bookDto.getAuthor(), registeredBook.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testListAllBooks() {
        // Given
        Book book1 = new Book(UUID.randomUUID(), "978-3-16-148410-0", "Test Book 1", "Author 1", 1L);
        Book book2 = new Book(UUID.randomUUID(), "978-1-23-456789-0", "Test Book 2", "Author 2", 1L);
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // When
        List<BookDto> books = bookService.listAllBooks();

        // Then
        assertEquals(2, books.size());
        assertEquals(book1.getTitle(), books.get(0).getTitle());
        assertEquals(book2.getTitle(), books.get(1).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testFindBookById_Found() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book(bookId, "978-3-16-148410-0", "Test Book", "Inam", 1L);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        Book foundBook = bookService.findBookById(bookId);

        // Then
        assertNotNull(foundBook);
        assertEquals(bookId, foundBook.getId());
        assertEquals(book.getIsbn(), foundBook.getIsbn());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testFindBookById_NotFound() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> bookService.findBookById(bookId));
        assertEquals("Book not found with ID: " + bookId, exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
    }
}
