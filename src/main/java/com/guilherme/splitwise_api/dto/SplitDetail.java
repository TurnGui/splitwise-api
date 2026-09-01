package com.guilherme.splitwise_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SplitDetail {
    private Long userId;
    private BigDecimal value;
}