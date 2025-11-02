package com.estatement.util;

import com.estatement.dto.SearchCriteria;
import com.estatement.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class ExcelGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public byte[] generateTransactionExcel(List<Transaction> transactions, SearchCriteria criteria) throws IOException {
        log.info("Generating Excel workbook...");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        try {
            // Create Header Font and Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Currency Style
            CellStyle currencyCellStyle = workbook.createCellStyle();
            DataFormat currencyDataFormat = workbook.createDataFormat();
            currencyCellStyle.setDataFormat(currencyDataFormat.getFormat("#,##0.00"));
            currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Create Date Style
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-mm-yyyy hh:mm"));

            // --- Header Row ---
            String[] headers = {"Ref Number", "Date & Time", "Description", "Withdrawals", "Credit", "Balance"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- Data Rows ---
            int rowNum = 1;
            for (Transaction tx : transactions) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(tx.getTxnRefNumber());

                Cell dateCell = row.createCell(1);
                if (tx.getDateTime() != null) {
                    dateCell.setCellValue(tx.getDateTime()); // POI handles LocalDateTime directly
                    dateCell.setCellStyle(dateCellStyle);
                }

                row.createCell(2).setCellValue(tx.getDescription());

                Cell withdrawalsCell = row.createCell(3);
                withdrawalsCell.setCellValue(toDouble(tx.getWithdrawals()));
                withdrawalsCell.setCellStyle(currencyCellStyle);

                Cell creditCell = row.createCell(4);
                creditCell.setCellValue(toDouble(tx.getCredit()));
                creditCell.setCellStyle(currencyCellStyle);

                Cell balanceCell = row.createCell(5);
                balanceCell.setCellValue(toDouble(tx.getRunningBalance()));
                balanceCell.setCellStyle(currencyCellStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            log.info("Excel sheet created with {} data rows.", transactions.size());

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error occurred during Excel generation: {}", e.getMessage(), e);
            throw new IOException("Failed to generate Excel document", e);
        } finally {
            workbook.close(); // Important to close the workbook
            log.debug("Excel Workbook closed.");
        }
    }

    // Helper to convert BigDecimal to double for POI, handling nulls
    private double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}