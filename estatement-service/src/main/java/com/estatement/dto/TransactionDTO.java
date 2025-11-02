package com.estatement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String txnRefNumber;
    private String accountNumber;
    private LocalDateTime dateTime;
    private String description;
    private BigDecimal withdrawals;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    private String formattedDate;
    private String formattedWithdrawals;
    private String formattedCredit;
    private String formattedBalance;
}