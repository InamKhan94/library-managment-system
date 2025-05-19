package com.library.management.system.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.system.dto.BookDto;
import com.library.management.system.model.Book;
import com.library.management.system.repository.BookRepository;
import com.library.management.system.repository.BorrowingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Run only for 'dev' profile
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        borrowingRepository.deleteAll();
        bookRepository.deleteAll(); // Clean up the database before each test
        entityManager.flush();
        entityManager.clear(); // Clear the persistence context to avoid stale entities

    }

    @Test
    @Transactional
    void registerBook_Success() throws Exception {
        // Arrange
        BookDto bookDto = new BookDto(null, "123-456-789", "Effective Java", "Joshua Bloch");

        // Act & Assert
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isbn", is("123-456-789")))
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.author", is("Joshua Bloch")));
    }

    @Test
    @Transactional()
    void getAllBooks_Success() throws Exception {
        // Arrange
        Book book1 = Book.builder()
                .isbn("123-456-789")
                .title("Effective Java")
                .author("Joshua Bloch")
                .version(0L) // Initial version
                .build();

        Book book2 = Book.builder()
                .isbn("987-654-321")
                .title("Clean Code")
                .author("Robert C. Martin")
                .version(0L) // Initial version
                .build();
        bookRepository.saveAll(List.of(book1, book2));

        // Act & Assert
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].isbn", is("123-456-789")))
                .andExpect(jsonPath("$[0].title", is("Effective Java")))
                .andExpect(jsonPath("$[0].author", is("Joshua Bloch")))
                .andExpect(jsonPath("$[1].isbn", is("987-654-321")))
                .andExpect(jsonPath("$[1].title", is("Clean Code")))
                .andExpect(jsonPath("$[1].author", is("Robert C. Martin")));
    }
}
