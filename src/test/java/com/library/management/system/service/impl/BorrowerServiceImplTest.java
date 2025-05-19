package com.library.management.system.service.impl;

import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.exception.ResourceNotFoundException;
import com.library.management.system.model.Borrower;
import com.library.management.system.repository.BorrowerRepository;
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

class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterBorrower() {
        // Given
        BorrowerDto borrowerDto = BorrowerDto.builder()
                .name("Inam")
                .email("ullahkhaninam@gmail.com")
                .build();

        Borrower savedBorrower = new Borrower(UUID.randomUUID(), "Inam", "ullahkhaninam@gmail.com");

        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        // When
        BorrowerDto registeredBorrower = borrowerService.registerBorrower(borrowerDto);

        // Then
        assertNotNull(registeredBorrower.getId());
        assertEquals(borrowerDto.getName(), registeredBorrower.getName());
        assertEquals(borrowerDto.getEmail(), registeredBorrower.getEmail());
        verify(borrowerRepository, times(1)).save(any(Borrower.class));
    }

    @Test
    void testGetAllBorrowers() {
        // Given
        Borrower borrower1 = new Borrower(UUID.randomUUID(), "Inam", "ullahkhaninam@gmail.com");
        Borrower borrower2 = new Borrower(UUID.randomUUID(), "Khan", "khan@gmail.com");
        when(borrowerRepository.findAll()).thenReturn(Arrays.asList(borrower1, borrower2));

        // When
        List<BorrowerDto> borrowers = borrowerService.getAllBorrowers();

        // Then
        assertEquals(2, borrowers.size());
        assertEquals(borrower1.getName(), borrowers.get(0).getName());
        assertEquals(borrower2.getName(), borrowers.get(1).getName());
        verify(borrowerRepository, times(1)).findAll();
    }

    @Test
    void testFindBorrowerById_Found() {
        // Given
        UUID borrowerId = UUID.randomUUID();
        Borrower borrower = new Borrower(borrowerId, "Inam", "ullahkhaninam@gmail.com");
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));

        // When
        Borrower foundBorrower = borrowerService.findBorrowerById(borrowerId);

        // Then
        assertNotNull(foundBorrower);
        assertEquals(borrowerId, foundBorrower.getId());
        assertEquals(borrower.getName(), foundBorrower.getName());
        assertEquals(borrower.getEmail(), foundBorrower.getEmail());
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }

    @Test
    void testFindBorrowerById_NotFound() {
        // Given
        UUID borrowerId = UUID.randomUUID();
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> borrowerService.findBorrowerById(borrowerId));
        assertEquals("Borrower not found with ID: " + borrowerId, exception.getMessage());
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }
}
