package com.estatement.service;

import com.estatement.dto.SearchCriteria;
import com.estatement.entity.Transaction;
import com.estatement.util.ExcelGenerator;
import com.estatement.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // Ensure this is imported
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final SearchService searchService;
    private final PdfGenerator pdfGenerator;
    private final ExcelGenerator excelGenerator;

    private static final int MAX_EXPORT_SIZE = 10000;

    public byte[] exportTransactionsToPdf(SearchCriteria criteria) throws IOException {
        List<Transaction> transactions = fetchAllTransactionsForExport(criteria);
        if (transactions.isEmpty()) return null;
        return pdfGenerator.generateTransactionPdf(transactions, criteria);
    }

    public byte[] exportTransactionsToExcel(SearchCriteria criteria) throws IOException {
        List<Transaction> transactions = fetchAllTransactionsForExport(criteria);
        if (transactions.isEmpty()) return null;
        return excelGenerator.generateTransactionExcel(transactions, criteria);
    }

    private List<Transaction> fetchAllTransactionsForExport(SearchCriteria criteria) {
        List<Transaction> allTransactions = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 100;
        Page<Transaction> page; // This variable is now the correct type

        log.debug("Fetching transactions for export...");

        do {
            criteria.setPage(pageNumber);
            criteria.setSize(pageSize);
            if (criteria.getSortBy() == null) {
                criteria.setSortBy("dateTime");
                criteria.setSortDirection("ASC");
            }

            page = searchService.searchTransactionsForExport(criteria);


            if (page.hasContent()) {
                allTransactions.addAll(page.getContent());
            }
            pageNumber++;
            if (allTransactions.size() >= MAX_EXPORT_SIZE) {
                log.warn("Export limit ({}) reached.", MAX_EXPORT_SIZE);
                break;
            }
        } while (page.hasNext());

        log.debug("Fetched {} total transactions for export.", allTransactions.size());
        return allTransactions;
    }
}