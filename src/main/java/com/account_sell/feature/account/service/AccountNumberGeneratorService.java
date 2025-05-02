package com.account_sell.feature.account.service;

import com.account_sell.feature.account.dto.request.GenerateAccountRequest;
import com.account_sell.feature.account.dto.resposne.GenerateAccountResponse;

/**
 * Service interface for generating special account numbers
 */
public interface AccountNumberGeneratorService {

    /**
     * Generates special account numbers based on the requested criteria
     * 
     * @param request the request with pattern and price range criteria
     * @param limit maximum number of account numbers to generate
     * @return response containing generated account numbers
     */
    GenerateAccountResponse generateSpecialAccountNumbers(GenerateAccountRequest request, int limit);
}