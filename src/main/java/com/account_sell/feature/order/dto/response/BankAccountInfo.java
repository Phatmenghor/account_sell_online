package com.account_sell.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountInfo {
    private String currency;
    private String accountTitle;
    private String customerId;
    private String statusCode;
    private String phoneNumber;
    private String openingDate;
    private boolean accountExists;
    
    // Determine if account is active and valid
    public boolean isActive() {
        return accountExists && "00".equals(statusCode);
    }
}