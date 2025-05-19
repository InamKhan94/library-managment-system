package com.library.management.system.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.system.model.Book;
import com.library.management.system.model.Borrower;
import com.library.management.system.repository.BookRepository;
import com.library.management.system.repository.BorrowerRepository;
import com.library.management.system.repository.BorrowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev") // Run only for 'dev' profile
class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID borrowerId;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        borrowingRepository.deleteAll();
        borrowerRepository.deleteAll();
        bookRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create a test borrower
        Borrower borrower = Borrower.builder()
                .name("Inam")
                .email("ullahkhaninam@gmail.com")
                .version(0L) // Initial version
                .build();
        borrower = borrowerRepository.save(borrower);
        borrowerId = borrower.getId();

        // Create a test book
        Book book = Book.builder()
                .isbn("123-456-789")
                .title("Effective Java")
                .author("Joshua Bloch")
                .version(0L) // Initial version
                .build();

        book = bookRepository.save(book);
        bookId = book.getId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void borrowBook_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/books/borrower/" + borrowerId + "/" + bookId + "/borrow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.borrowerId", is(borrowerId.toString())))
                .andExpect(jsonPath("$.bookId", is(bookId.toString())))
                .andExpect(jsonPath("$.borrowerName", is("Inam")))
                .andExpect(jsonPath("$.borrowerEmail", is("ullahkhaninam@gmail.com")))
                .andExpect(jsonPath("$.bookTitle", is("Effective Java")))
                .andExpect(jsonPath("$.bookAuthor", is("Joshua Bloch")))
                .andExpect(jsonPath("$.bookIsbn", is("123-456-789")))
                .andExpect(jsonPath("$.borrowDate", notNullValue()))
                .andExpect(jsonPath("$.returnDate", nullValue())); // Not yet returned

    }

    @Test
    void returnBook_Success() throws Exception {
        mockMvc.perform(post("/api/books/borrower/" + borrowerId + "/" + bookId + "/borrow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books/borrower/" + borrowerId + "/" + bookId + "/return")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.borrowerId", is(borrowerId.toString())))
                .andExpect(jsonPath("$.bookId", is(bookId.toString())))
                .andExpect(jsonPath("$.borrowerName", is("Inam")))
                .andExpect(jsonPath("$.borrowerEmail", is("ullahkhaninam@gmail.com")))
                .andExpect(jsonPath("$.bookTitle", is("Effective Java")))
                .andExpect(jsonPath("$.bookAuthor", is("Joshua Bloch")))
                .andExpect(jsonPath("$.bookIsbn", is("123-456-789")))
                .andExpect(jsonPath("$.borrowDate", notNullValue()))
                .andExpect(jsonPath("$.returnDate", notNullValue())); // Not yet returned
    }

    @Test
    void borrowBook_AlreadyBorrowed_ThrowsException() throws Exception {
        // First borrow the book
        mockMvc.perform(post("/api/books/borrower/" + borrowerId + "/" + bookId + "/borrow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Try borrowing again, should fail
        mockMvc.perform(post("/api/books/borrower/" + borrowerId + "/" + bookId + "/borrow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // assuming controller handles exception and returns 400
                .andExpect(content().string(containsString("Book is already borrowed")));
    }

}
