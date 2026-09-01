package com.guilherme.splitwise_api.dto;

import com.guilherme.splitwise_api.model.SplitType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateExpenseRequest {
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private Long groupId;
    private Long paidById;
    private SplitType splitType;
    private List<SplitDetail> splitDetails;
}