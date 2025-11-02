package com.estatement.controller;

import com.estatement.dto.PageResponse;
import com.estatement.dto.TransactionDTO;
import com.estatement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("GET /transactions - Page: {}, Size: {}", page, size);
        PageResponse<TransactionDTO> response = transactionService.getAllTransactions(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<PageResponse<TransactionDTO>> getTransactionsByAccount(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("GET /transactions/account/{} - Page: {}, Size: {}", accountNumber, page, size);
        PageResponse<TransactionDTO> response = transactionService.getTransactionsByAccount(
                accountNumber, page, size, sortBy, sortDirection
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        log.info("GET /transactions/{}", id);
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<String>> getAllAccountNumbers() {
        log.info("GET /transactions/accounts");
        List<String> accountNumbers = transactionService.getAllAccountNumbers();
        return ResponseEntity.ok(accountNumbers);
    }
}