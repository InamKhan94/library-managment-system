package com.library.management.system.service.impl;

import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.exception.ResourceNotFoundException;
import com.library.management.system.model.Book;
import com.library.management.system.model.Borrower;
import com.library.management.system.repository.BookRepository;
import com.library.management.system.repository.BorrowerRepository;
import com.library.management.system.service.BorrowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Autowired
    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public BorrowerDto registerBorrower(BorrowerDto borrowerDto) {
        log.info("Registering new borrower: {}", borrowerDto);
        return mapToDto(borrowerRepository.save(mapToEntity(borrowerDto)));
    }

    @Override
    public List<BorrowerDto> getAllBorrowers() {
        List<Borrower> borrowers = borrowerRepository.findAll();
        log.info("Fetched {} borrowers from the database", borrowers.size());
        return borrowers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Borrower findBorrowerById(UUID borrowerId) {
        log.info("Searching for borrower with ID: {}", borrowerId);
        return borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> {
                    log.error("borrower not found with ID: {}", borrowerId);
                    return new ResourceNotFoundException("Borrower not found with ID: " + borrowerId);
                });
    }

    private BorrowerDto mapToDto(Borrower borrower) {
        return BorrowerDto.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .build();
    }
}
