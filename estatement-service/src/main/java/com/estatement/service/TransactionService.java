package com.estatement.service;

import com.estatement.dto.PageResponse;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Transactional(readOnly = true)
    public PageResponse<TransactionDTO> getAllTransactions(int page, int size, String sortBy, String sortDirection) {
        log.debug("Getting all transactions - Page: {}, Size: {}", page, size);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        return convertToPageResponse(transactionPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionDTO> getTransactionsByAccount(
            String accountNumber, int page, int size, String sortBy, String sortDirection) {
        log.debug("Getting transactions for account: {}", accountNumber);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactionPage = transactionRepository.findByAccountNumber(accountNumber, pageable);

        return convertToPageResponse(transactionPage);
    }

    @Transactional(readOnly = true)
    public List<String> getAllAccountNumbers() {
        log.debug("Getting all unique account numbers");
        return transactionRepository.findAllUniqueAccountNumbers();
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        log.debug("Getting transaction by id: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return convertToDTO(transaction);
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
}