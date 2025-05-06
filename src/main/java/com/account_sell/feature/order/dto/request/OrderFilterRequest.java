package com.account_sell.feature.order.dto.request;

import com.account_sell.enumation.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {
    private int pageNo = 1;
    private int pageSize = 10;
    private OrderStatus status;
    private String search;
}