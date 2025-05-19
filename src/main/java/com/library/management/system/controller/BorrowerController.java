package com.library.management.system.controller;

import com.library.management.system.dto.BorrowerDto;
import com.library.management.system.model.Borrower;
import com.library.management.system.service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    @PostMapping
    public ResponseEntity<BorrowerDto> registerBorrower(@RequestBody BorrowerDto borrowerDto) {
        return ResponseEntity.ok(borrowerService.registerBorrower(borrowerDto));
    }

    @GetMapping
    public ResponseEntity<List<BorrowerDto>> getAllBooks() {
        return ResponseEntity.ok(borrowerService.getAllBorrowers());
    }
}