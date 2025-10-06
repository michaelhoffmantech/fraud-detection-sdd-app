package com.globalfraud.frauddetection.repository;

import com.globalfraud.frauddetection.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByCustomerId(String customerId);

    List<Transaction> findByMerchantId(String merchantId);

    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId "
            + "AND t.transactionTimestamp BETWEEN :startTime AND :endTime")
    List<Transaction> findTransactionsByCustomerInTimeWindow(@Param("customerId") String customerId,
            @Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT t FROM Transaction t WHERE t.merchantId = :merchantId "
            + "AND t.transactionTimestamp BETWEEN :startTime AND :endTime")
    List<Transaction> findTransactionsByMerchantInTimeWindow(@Param("merchantId") String merchantId,
            @Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);
}
