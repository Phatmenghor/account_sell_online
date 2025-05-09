package com.account_sell.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountNumberResponse {
    private boolean isValid;
    private String validBy;
    private boolean isAvailable;
    private String accountNumber;
    private BigDecimal price;
    private String message;
}