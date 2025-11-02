package com.estatement.service;

import com.estatement.entity.Transaction;
import com.estatement.entity.Account;
import com.estatement.repository.TransactionRepository;
import com.estatement.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    // BalanceCalculationService is removed as it's not used here

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Transactional
    public int processAndInsertTransactions(String filename, InputStream inputStream, Long fileUploadId) {
        log.info("Starting CSV processing for file: {} (Upload ID: {})", filename, fileUploadId);

        List<Transaction> allParsedTransactions = new ArrayList<>();

        try {
            if (inputStream == null) {
                throw new RuntimeException("Input stream is null");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines()
                    .withTrim();

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            log.debug("CSV Header Map: {}", csvParser.getHeaderMap());

            for (CSVRecord record : csvParser) {
                try {
                    log.debug("Processing record {}", record.getRecordNumber());
                    Transaction transaction = parseCSVRecordToTransaction(record, fileUploadId);

                    if (transaction != null) {
                        allParsedTransactions.add(transaction);
                    }

                } catch (Exception e) {
                    log.warn("Error parsing CSV record at line {}: {}", record.getRecordNumber(), e.getMessage(), e);
                }
            }
            csvParser.close();
            reader.close();

            int parsedRecordCount = allParsedTransactions.size();
            log.info("Total records parsed from CSV: {}", parsedRecordCount);
            if (parsedRecordCount == 0) {
                log.warn("No records were parsed from the CSV file!");
                return 0;
            }

            // --- START OF DUPLICATE CHECK ---
            Set<String> parsedTxnRefNumbers = allParsedTransactions.stream()
                    .map(Transaction::getTxnRefNumber)
                    .collect(Collectors.toSet());
            Set<String> existingTxnRefNumbers = transactionRepository.findExistingTxnRefNumbers(parsedTxnRefNumbers);
            log.info("Found {} existing transactions. {} parsed in total.", existingTxnRefNumbers.size(), parsedTxnRefNumbers.size());

            List<Transaction> newTransactions = allParsedTransactions.stream()
                    .filter(t -> !existingTxnRefNumbers.contains(t.getTxnRefNumber()))
                    .collect(Collectors.toList());

            int newRecordCount = newTransactions.size();
            log.info("Found {} new transactions to insert.", newRecordCount);
            if (newRecordCount == 0) {
                log.warn("No new records to insert. All parsed transactions are duplicates or invalid.");
                return 0;
            }
            // --- END OF DUPLICATE CHECK ---

            // Get the set of affected account numbers
            Set<String> affectedAccountNumbers = newTransactions.stream()
                    .map(Transaction::getAccountNumber)
                    .collect(Collectors.toSet());

            // Create or update accounts
            log.info("Creating/updating {} accounts", affectedAccountNumbers.size());
            createOrUpdateAccounts(affectedAccountNumbers);

            // --- UPDATED LOGIC ---
            // 1. Save new transactions first (with 0 balance)
            log.info("Saving {} new transactions to database...", newTransactions.size());
            transactionRepository.saveAll(newTransactions);
            log.info("New transactions saved.");

            // 2. Recalculate balances for all affected accounts
            log.info("Recalculating balances for {} affected accounts...", affectedAccountNumbers.size());
            for (String accountNumber : affectedAccountNumbers) {
                recalculateBalancesForAccount(accountNumber);
            }
            // --- END OF UPDATED LOGIC ---

            log.info("Successfully processed and inserted {} new transactions", newRecordCount);
            return newRecordCount;

        } catch (Exception e) {
            log.error("Error processing CSV file: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
        }
    }

    private Transaction parseCSVRecordToTransaction(CSVRecord record, Long fileUploadId) {
        try {
            Transaction transaction = new Transaction();
            String txnRefNumber = getFieldValue(record, "Txn Ref Number");
            String accountNumber = getFieldValue(record, "Account Number");
            String dateTimeStr = getFieldValue(record, "Date Time");
            String description = getFieldValue(record, "Description");
            String withdrawalsStr = getFieldValue(record, "Withdrawals");
            String creditStr = getFieldValue(record, "Credit");

            if (txnRefNumber == null || txnRefNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction reference number is required");
            }
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number is required");
            }
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Date/Time is required");
            }

            transaction.setTxnRefNumber(txnRefNumber.trim());
            transaction.setAccountNumber(accountNumber.trim());
            transaction.setDescription(description != null ? description.trim() : "");
            transaction.setFileUploadId(fileUploadId);

            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
                transaction.setDateTime(dateTime);
            } catch (Exception e) {
                log.warn("Invalid date format '{}' for record {}: {}", dateTimeStr, record.getRecordNumber(), e.getMessage());
                throw new IllegalArgumentException("Invalid date format: " + dateTimeStr);
            }

            BigDecimal withdrawals = BigDecimal.ZERO;
            if (withdrawalsStr != null && !withdrawalsStr.trim().isEmpty()) {
                try {
                    withdrawals = new BigDecimal(withdrawalsStr.trim());
                    if (withdrawals.compareTo(BigDecimal.ZERO) < 0) {
                        withdrawals = BigDecimal.ZERO;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid withdrawals amount '{}' for record {}", withdrawalsStr, record.getRecordNumber());
                }
            }
            transaction.setWithdrawals(withdrawals);

            BigDecimal credit = BigDecimal.ZERO;
            if (creditStr != null && !creditStr.trim().isEmpty()) {
                try {
                    credit = new BigDecimal(creditStr.trim());
                    if (credit.compareTo(BigDecimal.ZERO) < 0) {
                        credit = BigDecimal.ZERO;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid credit amount '{}' for record {}", creditStr, record.getRecordNumber());
                }
            }
            transaction.setCredit(credit);
            transaction.setRunningBalance(BigDecimal.ZERO); // Set default
            return transaction;
        } catch (Exception e) {
            log.error("Error parsing CSV record at line {}: {}", record.getRecordNumber(), e.getMessage());
            return null;
        }
    }

    private String getFieldValue(CSVRecord record, String fieldName) {
        try {
            if (record.isMapped(fieldName)) {
                return record.get(fieldName);
            }
            String withBOM = "\ufeff" + fieldName;
            if (record.isMapped(withBOM)) {
                return record.get(withBOM);
            }
            for (int i = 0; i < record.size(); i++) {
                String header = record.getParser().getHeaderNames().get(i);
                if (header != null && header.replaceAll("\ufeff", "").equals(fieldName)) {
                    return record.get(i);
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void createOrUpdateAccounts(Set<String> accountNumbers) {
        log.info("Creating/updating {} accounts", accountNumbers.size());
        for (String accountNumber : accountNumbers) {
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                Account newAccount = new Account();
                newAccount.setAccountNumber(accountNumber);
                newAccount.setCurrentBalance(BigDecimal.ZERO);
                accountRepository.save(newAccount);
            }
        }
        log.debug("Accounts created/updated successfully");
    }

    // --- REMOVED THE FLAWED calculateAndAssignRunningBalances METHOD ---

    private BigDecimal getLastRunningBalance(String accountNumber) {
        // This method is now only used by the *new* recalculateBalancesForAccount
        // to find the balance *before* the new transactions
        Transaction lastTransaction = transactionRepository.findFirstByAccountNumberOrderByDateTimeDesc(accountNumber);
        if (lastTransaction != null && lastTransaction.getRunningBalance() != null) {
            return lastTransaction.getRunningBalance();
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    public void recalculateAllRunningBalances() {
        log.info("Starting recalculation of all running balances");
        List<String> accountNumbers = transactionRepository.findAllUniqueAccountNumbers();
        for (String accountNumber : accountNumbers) {
            recalculateBalancesForAccount(accountNumber);
        }
        log.info("Recalculation complete for {} accounts", accountNumbers.size());
    }

    // --- THIS METHOD IS NOW UPDATED ---
    @Transactional
    public void recalculateBalancesForAccount(String accountNumber) {
        log.info("Recalculating balances for account: {}", accountNumber);

        // 1. Lock the account to prevent race conditions
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found for update: " + accountNumber));

        // 2. Get all transactions for this account, sorted by date
        List<Transaction> transactions = transactionRepository
                .findByAccountNumber(accountNumber, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
                .stream()
                .sorted(Comparator.comparing(Transaction::getDateTime))
                .collect(Collectors.toList());

        BigDecimal runningBalance = BigDecimal.ZERO;

        // 3. Loop through ALL transactions and recalculate running balance
        for (Transaction transaction : transactions) {
            runningBalance = runningBalance
                    .add(transaction.getCredit() != null ? transaction.getCredit() : BigDecimal.ZERO)
                    .subtract(transaction.getWithdrawals() != null ? transaction.getWithdrawals() : BigDecimal.ZERO);

            transaction.setRunningBalance(runningBalance);
        }

        // 4. Save the updated transactions
        transactionRepository.saveAll(transactions);

        // 5. UPDATE THE ACCOUNT'S CURRENT BALANCE
        account.setCurrentBalance(runningBalance);
        accountRepository.save(account);

        log.debug("Recalculated {} transactions for account {}. Final balance: {}", transactions.size(), accountNumber, runningBalance);
    }
}
