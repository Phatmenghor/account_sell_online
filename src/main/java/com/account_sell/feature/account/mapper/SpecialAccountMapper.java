package com.account_sell.feature.account.mapper;

import com.account_sell.feature.account.dto.resposne.GenerateAccountResponse;
import com.account_sell.utils.PatternUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

/**
 * MapStruct mapper for converting between account entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface SpecialAccountMapper {

    /**
     * Maps an account number entry to a response DTO
     * @param entry the account number with price
     * @return the account details response object
     */
    @Mapping(source = "key", target = "accountNumber")
    @Mapping(source = "value", target = "price")
    @Mapping(source = "value", target = "priceRange", qualifiedByName = "toPriceRange")
    GenerateAccountResponse.AccountDetails toAccountDetails(Map.Entry<String, Double> entry);

    /**
     * Maps a list of account number entries to account details
     * @param entries list of account number entries
     * @return list of account details
     */
    List<GenerateAccountResponse.AccountDetails> toAccountDetailsList(List<Map.Entry<String, Double>> entries);

    /**
     * Convert a price to a price range description
     * @param price the price value
     * @return the price range description
     */
    @Named("toPriceRange")
    default String toPriceRange(Double price) {
        return PatternUtil.getPriceRangeDescription(price);
    }
}