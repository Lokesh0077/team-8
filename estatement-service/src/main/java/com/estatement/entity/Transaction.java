package com.estatement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_account_date", columnList = "account_number, date_time"),
        @Index(name = "idx_txn_ref", columnList = "txn_ref_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_ref_number", nullable = false, length = 50)
    private String txnRefNumber;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "withdrawals", precision = 15, scale = 2)
    private BigDecimal withdrawals = BigDecimal.ZERO;

    @Column(name = "credit", precision = 15, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    @Column(name = "running_balance", precision = 15, scale = 2)
    private BigDecimal runningBalance = BigDecimal.ZERO;

    @Column(name = "file_upload_id")
    private Long fileUploadId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}