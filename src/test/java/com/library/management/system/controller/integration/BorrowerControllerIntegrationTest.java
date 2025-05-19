package com.library.management.system.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.model.Borrower;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Run only for 'dev' profile
class BorrowerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        borrowingRepository.deleteAll();
        borrowerRepository.deleteAll(); // Clean up the database before each test
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @Transactional
    void registerBorrower_Success() throws Exception {
        // Arrange
        BorrowerDto borrowerDto = new BorrowerDto(null, "Inam", "ullahkhaninam@gmail.com");

        // Act & Assert
        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowerDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Inam")))
                .andExpect(jsonPath("$.email", is("ullahkhaninam@gmail.com")));
    }

    @Test
    @Transactional
    void getAllBorrowers_Success() throws Exception {
        // Arrange
        Borrower borrower1 = Borrower.builder()
                .name("Inam")
                .email("ullahkhaninam@gmail.com")
                .version(0L) // Initial version
                .build();

        Borrower borrower2 = Borrower.builder()
                .name("khan")
                .email("khan@gmail.com")
                .version(0L) // Initial version
                .build();

        borrowerRepository.saveAll(List.of(borrower1, borrower2));

        // Act & Assert
        mockMvc.perform(get("/api/borrowers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is("Inam")))
                .andExpect(jsonPath("$[0].email", is("ullahkhaninam@gmail.com")))
                .andExpect(jsonPath("$[1].name", is("khan")))
                .andExpect(jsonPath("$[1].email", is("khan@gmail.com")));
    }
}
