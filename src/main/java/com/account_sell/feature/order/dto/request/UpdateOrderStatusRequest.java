package com.account_sell.feature.order.dto.request;

import com.account_sell.enumation.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    private String remarks;
}