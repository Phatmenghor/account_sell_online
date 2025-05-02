package com.account_sell.feature.account.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAccountResponse {
    private List<AccountDetails> accountNumberDetails;
    private long totalCount;
    private int generatedCount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDetails {
        private String accountNumber;
        private double price;
        private String priceRange;
    }
}