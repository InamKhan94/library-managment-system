package com.library.management.system.service.impl;

import com.library.management.system.dto.BookDto;
import com.library.management.system.exception.ResourceNotFoundException;
import com.library.management.system.model.Book;
import com.library.management.system.repository.BookRepository;
import com.library.management.system.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDto registerBook(BookDto bookDto) {
        log.info("Registering new book: {}", bookDto);
        try {
            return mapToDto((bookRepository.save(mapToEntity(bookDto))));
        }
        catch (ObjectOptimisticLockingFailureException e) {
            log.info("Update conflict: The book was modified by another user");
            throw new IllegalStateException("Update conflict: The book was modified by another user.");
        }
    }

    public List<BookDto> listAllBooks() {
        List<Book> books = bookRepository.findAll();
        log.info("Fetched {} books from the database", books.size());
        return books.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Book findBookById(UUID bookId) {
        log.info("Searching for book with ID: {}", bookId);
        return bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found with ID: {}", bookId);
                    return new ResourceNotFoundException("Book not found with ID: " + bookId);
                });
    }

    private BookDto mapToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }
}
