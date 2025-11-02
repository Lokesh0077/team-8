package com.estatement.service;

import com.estatement.dto.PageResponse;
import com.estatement.dto.SearchCriteria;
import com.estatement.dto.TransactionDTO;
import com.estatement.entity.Transaction;
import com.estatement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final TransactionRepository transactionRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Transactional(readOnly = true)
    public PageResponse<TransactionDTO> searchTransactions(SearchCriteria criteria) {
        log.debug("Searching transactions with criteria: {}", criteria);

        Sort sort = criteria.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.by(criteria.getSortBy()).ascending()
                : Sort.by(criteria.getSortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        Page<Transaction> transactionPage = transactionRepository.advancedSearch(
                criteria.getAccountNumber(),
                criteria.getStartDate(),
                criteria.getEndDate(),
                criteria.getDescription(),
                criteria.getTxnRefNumber(),
                pageable
        );

        return convertToPageResponse(transactionPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionDTO> searchByAccountAndDateRange(
            String accountNumber, LocalDateTime startDate, LocalDateTime endDate,
            int page, int size) {
        log.debug("Searching by account: {} and date range: {} to {}", accountNumber, startDate, endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        Page<Transaction> transactionPage = transactionRepository.findByAccountNumberAndDateRange(
                accountNumber, startDate, endDate, pageable
        );

        return convertToPageResponse(transactionPage);
    }

    private PageResponse<TransactionDTO> convertToPageResponse(Page<Transaction> transactionPage) {
        List<TransactionDTO> dtos = transactionPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResponse<TransactionDTO> response = new PageResponse<>();
        response.setContent(dtos);
        response.setCurrentPage(transactionPage.getNumber());
        response.setTotalPages(transactionPage.getTotalPages());
        response.setTotalElements(transactionPage.getTotalElements());
        response.setPageSize(transactionPage.getSize());
        response.setFirst(transactionPage.isFirst());
        response.setLast(transactionPage.isLast());
        response.setEmpty(transactionPage.isEmpty());

        return response;
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTxnRefNumber(transaction.getTxnRefNumber());
        dto.setAccountNumber(transaction.getAccountNumber());
        dto.setDateTime(transaction.getDateTime());
        dto.setDescription(transaction.getDescription());
        dto.setWithdrawals(transaction.getWithdrawals());
        dto.setCredit(transaction.getCredit());
        dto.setRunningBalance(transaction.getRunningBalance());

        // Formatted strings for display
        dto.setFormattedDate(transaction.getDateTime().format(DATE_FORMATTER));
        dto.setFormattedWithdrawals(formatCurrency(transaction.getWithdrawals()));
        dto.setFormattedCredit(formatCurrency(transaction.getCredit()));
        dto.setFormattedBalance(formatCurrency(transaction.getRunningBalance()));

        return dto;
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) == 0) {
            return "0.00";
        }
        return String.format("%.2f", amount);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> searchTransactionsForExport(SearchCriteria criteria) {
        log.debug("Searching transactions for export with criteria: {}", criteria);

        Sort sort = criteria.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.by(criteria.getSortBy()).ascending()
                : Sort.by(criteria.getSortBy()).descending();

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);

        return transactionRepository.advancedSearch(
                criteria.getAccountNumber(),
                criteria.getStartDate(),
                criteria.getEndDate(),
                criteria.getDescription(),
                criteria.getTxnRefNumber(),
                pageable
        );
    }
}