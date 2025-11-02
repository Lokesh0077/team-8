package com.estatement.util;

import com.estatement.dto.SearchCriteria;
import com.estatement.entity.Transaction;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class PdfGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    public byte[] generateTransactionPdf(List<Transaction> transactions, SearchCriteria criteria) throws IOException {
        log.info("Generating PDF document...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            Paragraph title = new Paragraph("Account Statement")
                    .setBold()
                    .setFontSize(18);
            document.add(title);

            document.add(new Paragraph("Account Number: " + criteria.getAccountNumber()).setFontSize(10));
            document.add(new Paragraph("Period: " + formatDate(criteria.getStartDate()) + " to " +
                    formatDate(criteria.getEndDate())).setFontSize(10));

            float[] columnWidths = {100f, 120f, 180f, 80f, 80f, 80f};
            Table table = new Table(columnWidths);

            addHeaderCell(table, "Ref Number");
            addHeaderCell(table, "Date & Time");
            addHeaderCell(table, "Description");
            addHeaderCell(table, "Withdrawals");
            addHeaderCell(table, "Credit");
            addHeaderCell(table, "Balance");

            for (Transaction tx : transactions) {
                table.addCell(new Cell().add(new Paragraph(tx.getTxnRefNumber())).setFontSize(8));
                table.addCell(new Cell().add(new Paragraph(formatDateTime(tx.getDateTime()))).setFontSize(8));
                table.addCell(new Cell().add(new Paragraph(tx.getDescription())).setFontSize(8));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(tx.getWithdrawals()))).setFontSize(8));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(tx.getCredit()))).setFontSize(8));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(tx.getRunningBalance()))).setFontSize(8));
            }

            document.add(table);
            log.info("PDF table created with {} rows.", transactions.size());

        } catch (Exception e) {
            log.error("Error occurred during PDF generation: {}", e.getMessage(), e);
            throw new IOException("Failed to generate PDF document", e);
        } finally {
            document.close();
            log.debug("PDF Document closed.");
        }

        return baos.toByteArray();
    }

    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addHeaderCell(cell);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A";
    }

    private String formatCurrency(BigDecimal amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : "0.00";
    }
}
