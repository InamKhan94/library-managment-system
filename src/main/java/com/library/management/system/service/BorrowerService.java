package com.library.management.system.service;

import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.model.Borrower;

import java.util.List;
import java.util.UUID;

public interface BorrowerService {

    BorrowerDto registerBorrower(BorrowerDto borrowerDto);

    List<BorrowerDto> getAllBorrowers();

    Borrower findBorrowerById(UUID borrowerId);

    default Borrower mapToEntity(BorrowerDto borrowerDto) {
        return Borrower.builder()
                .id(borrowerDto.getId())
                .name(borrowerDto.getName())
                .email(borrowerDto.getEmail())
                .version(0L)
                .build();
    }
}
