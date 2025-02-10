package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Card;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Card> getCardsByAccount(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    public Card addCardToAccount(Long accountId, Card card) throws CardAlreadyExistsException {
        Account account = accountRepository.findById(accountId)
                         .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        Card existingCard = cardRepository.findByAccountIdAndCardNumber(accountId, card.getCardNumber());
        if (existingCard != null) {
            throw new CardAlreadyExistsException();
        }
        card.setAccount(account);
        return cardRepository.save(card);
    }

    public void deleteCard(Long accountId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
        if (!card.getAccount().getId().equals(accountId)) {
            throw new IllegalArgumentException("La tarjeta no pertenece a la cuenta especificada");
        }
        cardRepository.delete(card);
    }

    public static class CardAlreadyExistsException extends Exception { }
}

