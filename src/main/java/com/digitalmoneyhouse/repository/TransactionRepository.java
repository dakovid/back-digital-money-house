package com.digitalmoneyhouse.repository;

import com.digitalmoneyhouse.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop5ByAccountIdOrderByDateDesc(Long accountId);
    List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);
}

