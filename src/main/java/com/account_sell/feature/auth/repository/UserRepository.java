package com.account_sell.feature.auth.repository;

import com.account_sell.enumation.StatusData;
import com.account_sell.feature.auth.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);

    Page<UserEntity> findByUsernameContainingIgnoreCaseAndStatus(String username, StatusData status, Pageable pageable);
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<UserEntity> findByStatus(StatusData status, Pageable pageable);

}
