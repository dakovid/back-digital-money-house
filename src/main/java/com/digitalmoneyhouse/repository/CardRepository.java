package com.digitalmoneyhouse.repository;

import com.digitalmoneyhouse.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long accountId);
    Card findByAccountIdAndCardNumber(Long accountId, String cardNumber);
}

