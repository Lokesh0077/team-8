package com.estatement.controller;

import com.estatement.dto.PageResponse;
import com.estatement.dto.SearchCriteria;
import com.estatement.dto.TransactionDTO;
import com.estatement.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<PageResponse<TransactionDTO>> searchTransactions(
            @RequestBody SearchCriteria criteria) {
        log.info("POST /search - Criteria: {}", criteria);
        PageResponse<TransactionDTO> response = searchService.searchTransactions(criteria);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/range")
    public ResponseEntity<PageResponse<TransactionDTO>> searchByDateRange(
            @RequestParam String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /search/range - Account: {}, Start: {}, End: {}", accountNumber, startDate, endDate);
        PageResponse<TransactionDTO> response = searchService.searchByAccountAndDateRange(
                accountNumber, startDate, endDate, page, size
        );
        return ResponseEntity.ok(response);
    }
}