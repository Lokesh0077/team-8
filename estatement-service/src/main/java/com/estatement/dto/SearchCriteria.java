package com.estatement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String accountNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String txnRefNumber;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "dateTime";
    private String sortDirection = "DESC";
}