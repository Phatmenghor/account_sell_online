package com.account_sell.feature.account.repository;

import com.account_sell.feature.account.models.SpecialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing special account numbers
 * Note: This is a placeholder. Actual implementation may vary 
 * depending on specific database storage requirements.
 */
@Repository
public interface SpecialAccountRepository extends JpaRepository<SpecialAccountEntity, Long> {
    // Custom query methods can be added here if needed
}