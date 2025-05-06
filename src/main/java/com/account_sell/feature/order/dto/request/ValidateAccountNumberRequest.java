package com.account_sell.feature.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountNumberRequest {
    @NotBlank(message = "Account number is required")
    @Size(min = 9, max = 9, message = "Account number must be exactly 9 digits")
    @Pattern(regexp = "\\d{9}", message = "Account number must contain only digits")
    private String accountNumber;
}