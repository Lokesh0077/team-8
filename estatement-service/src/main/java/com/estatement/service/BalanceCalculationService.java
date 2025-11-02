package com.estatement.service;

import com.estatement.entity.Transaction;
import com.estatement.entity.Account;
import com.estatement.repository.TransactionRepository;
import com.estatement.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceCalculationService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Calculate running balance for a specific account
     * Recalculates from the beginning to handle out-of-order file uploads
     */
    @Transactional
    public void calculateRunningBalanceForAccount(String accountNumber) {
        log.info("Calculating running balance for account: {}", accountNumber);

        try {
            // Get all transactions for this account, sorted by date and time
            List<Transaction> transactions = transactionRepository
                    .findByAccountNumber(accountNumber, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent()
                    .stream()
                    .sorted(Comparator.comparing(Transaction::getDateTime).thenComparing(Transaction::getId))
                    .collect(Collectors.toList());

            if (transactions.isEmpty()) {
                log.warn("No transactions found for account: {}", accountNumber);
                return;
            }

            BigDecimal runningBalance = BigDecimal.ZERO;

            for (Transaction transaction : transactions) {
                // Running balance = previous balance + credit - withdrawals
                BigDecimal credit = transaction.getCredit() != null ? transaction.getCredit() : BigDecimal.ZERO;
                BigDecimal withdrawals = transaction.getWithdrawals() != null ? transaction.getWithdrawals() : BigDecimal.ZERO;

                runningBalance = runningBalance.add(credit).subtract(withdrawals);
                transaction.setRunningBalance(runningBalance);

                log.debug("Account: {}, Date: {}, Debit: {}, Credit: {}, Balance: {}",
                        accountNumber, transaction.getDateTime(), withdrawals, credit, runningBalance);
            }

            // Save all updated transactions
            transactionRepository.saveAll(transactions);

            // Update account's current balance
            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
            account.setCurrentBalance(runningBalance);
            accountRepository.save(account);

            log.info("Running balance calculation completed for account: {}. Final balance: {}", accountNumber, runningBalance);

        } catch (Exception e) {
            log.error("Error calculating running balance for account {}: {}", accountNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to calculate running balance for account: " + accountNumber, e);
        }
    }

    /**
     * Recalculate running balances for all accounts
     * Useful when multiple files are uploaded out of order
     */
    @Transactional
    public void recalculateAllBalances() {
        log.info("Starting recalculation of running balances for all accounts");

        try {
            // Get all unique account numbers
            List<String> accountNumbers = transactionRepository.findAllUniqueAccountNumbers();

            log.info("Found {} unique accounts to recalculate", accountNumbers.size());

            for (String accountNumber : accountNumbers) {
                calculateRunningBalanceForAccount(accountNumber);
            }

            log.info("Recalculation of all running balances completed successfully");

        } catch (Exception e) {
            log.error("Error recalculating all balances: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to recalculate all balances", e);
        }
    }

    /**
     * Calculate running balance for a list of transactions
     * Used during CSV file processing
     */
    public void calculateRunningBalances(List<Transaction> transactions) {
        log.debug("Calculating running balances for {} transactions", transactions.size());

        if (transactions.isEmpty()) {
            return;
        }

        // Group transactions by account number
        var transactionsByAccount = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAccountNumber));

        for (var entry : transactionsByAccount.entrySet()) {
            String accountNumber = entry.getKey();
            List<Transaction> accountTransactions = entry.getValue();

            // Sort by date and time
            accountTransactions.sort(Comparator.comparing(Transaction::getDateTime));

            // Get the last running balance for this account (if any transactions exist before this batch)
            BigDecimal runningBalance = getLastRunningBalance(accountNumber);

            log.debug("Starting balance for account {}: {}", accountNumber, runningBalance);

            for (Transaction transaction : accountTransactions) {
                BigDecimal credit = transaction.getCredit() != null ? transaction.getCredit() : BigDecimal.ZERO;
                BigDecimal withdrawals = transaction.getWithdrawals() != null ? transaction.getWithdrawals() : BigDecimal.ZERO;

                runningBalance = runningBalance.add(credit).subtract(withdrawals);
                transaction.setRunningBalance(runningBalance);
            }

            log.debug("Final balance for account {}: {}", accountNumber, runningBalance);
        }
    }

    /**
     * Get the last running balance for an account from database
     * Used to continue balance calculation from previous transactions
     */
    private BigDecimal getLastRunningBalance(String accountNumber) {
        Transaction lastTransaction = transactionRepository.findFirstByAccountNumberOrderByDateTimeDesc(accountNumber);

        if (lastTransaction != null && lastTransaction.getRunningBalance() != null) {
            log.debug("Found last running balance for account {}: {}", accountNumber, lastTransaction.getRunningBalance());
            return lastTransaction.getRunningBalance();
        }

        log.debug("No previous transactions found for account {}, starting with 0.00", accountNumber);
        return BigDecimal.ZERO;
    }

    /**
     * Validate running balances for an account
     * Useful for debugging
     */
    public boolean validateRunningBalances(String accountNumber) {
        log.info("Validating running balances for account: {}", accountNumber);

        try {
            List<Transaction> transactions = transactionRepository
                    .findByAccountNumber(accountNumber, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent()
                    .stream()
                    .sorted(Comparator.comparing(Transaction::getDateTime))
                    .collect(Collectors.toList());

            BigDecimal expectedBalance = BigDecimal.ZERO;
            boolean isValid = true;

            for (Transaction transaction : transactions) {
                BigDecimal credit = transaction.getCredit() != null ? transaction.getCredit() : BigDecimal.ZERO;
                BigDecimal withdrawals = transaction.getWithdrawals() != null ? transaction.getWithdrawals() : BigDecimal.ZERO;

                expectedBalance = expectedBalance.add(credit).subtract(withdrawals);

                if (transaction.getRunningBalance() == null ||
                        transaction.getRunningBalance().compareTo(expectedBalance) != 0) {
                    log.warn("Balance mismatch for transaction ID {}: Expected {}, Got {}",
                            transaction.getId(), expectedBalance, transaction.getRunningBalance());
                    isValid = false;
                }
            }

            log.info("Running balance validation for account {}: {}", accountNumber, isValid ? "VALID" : "INVALID");
            return isValid;

        } catch (Exception e) {
            log.error("Error validating running balances for account {}: {}", accountNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Get the current balance for an account
     */
    public BigDecimal getCurrentBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        return account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;
    }

    /**
     * Update account balance
     */
    @Transactional
    public void updateAccountBalance(String accountNumber, BigDecimal balance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        account.setCurrentBalance(balance);
        accountRepository.save(account);

        log.info("Updated balance for account {} to: {}", accountNumber, balance);
    }
}