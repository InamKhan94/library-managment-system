package com.library.management.system.service;

import com.library.management.system.dto.BookDto;
import com.library.management.system.model.Book;

import java.util.List;
import java.util.UUID;

public interface BookService {

    BookDto registerBook(BookDto bookDto);

    List<BookDto> listAllBooks();

    Book findBookById(UUID bookId);

    default Book mapToEntity(BookDto bookDto) {
        return Book.builder()
                .id(bookDto.getId())
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .version(0L)
                .build();
    }
}
