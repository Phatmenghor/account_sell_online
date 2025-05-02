package com.account_sell.feature.account.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for storing generated special account numbers
 */
@Entity
@Table(name = "special_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 9)
    private String accountNumber;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
}