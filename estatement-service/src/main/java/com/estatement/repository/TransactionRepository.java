package com.estatement.repository;

import com.estatement.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find by account number with pagination
    Page<Transaction> findByAccountNumber(String accountNumber, Pageable pageable);

    // Find by account number and date range
    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = :accountNumber " +
            "AND t.dateTime BETWEEN :startDate AND :endDate ORDER BY t.dateTime DESC")
    Page<Transaction> findByAccountNumberAndDateRange(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Advanced search with multiple criteria
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:accountNumber IS NULL OR t.accountNumber = :accountNumber) AND " +
            "(:startDate IS NULL OR t.dateTime >= :startDate) AND " +
            "(:endDate IS NULL OR t.dateTime <= :endDate) AND " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:txnRefNumber IS NULL OR t.txnRefNumber = :txnRefNumber)")
    Page<Transaction> advancedSearch(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("description") String description,
            @Param("txnRefNumber") String txnRefNumber,
            Pageable pageable
    );

    // Get all unique account numbers
    @Query("SELECT DISTINCT t.accountNumber FROM Transaction t ORDER BY t.accountNumber")
    List<String> findAllUniqueAccountNumbers();

    // Count transactions by account
    long countByAccountNumber(String accountNumber);

    // Find latest transaction by account
    Transaction findFirstByAccountNumberOrderByDateTimeDesc(String accountNumber);

    @Query("SELECT t.txnRefNumber FROM Transaction t WHERE t.txnRefNumber IN :txnRefNumbers")
    Set<String> findExistingTxnRefNumbers(@Param("txnRefNumbers") Set<String> txnRefNumbers);
}