package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Card;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CardService cardService;

    private Account testAccount;
    private Card testCard;
    private final Long ACCOUNT_ID = 1L;
    private final Long CARD_ID = 1L;
    private final String CARD_NUMBER = "4111111111111111";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAccount = new Account();
        testAccount.setId(ACCOUNT_ID);

        testCard = new Card();
        testCard.setId(CARD_ID);
        testCard.setCardNumber(CARD_NUMBER);
        testCard.setAccount(testAccount);
    }

    @Test
    void getCardsByAccount_ReturnsCardList() {
        // Arrange
        List<Card> expectedCards = Arrays.asList(testCard);
        when(cardRepository.findByAccountId(ACCOUNT_ID)).thenReturn(expectedCards);

        // Act
        List<Card> result = cardService.getCardsByAccount(ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CARD_NUMBER, result.get(0).getCardNumber());
        verify(cardRepository).findByAccountId(ACCOUNT_ID);
    }

    @Test
    void getCardsByAccount_EmptyList_WhenNoCards() {
        // Arrange
        when(cardRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Arrays.asList());

        // Act
        List<Card> result = cardService.getCardsByAccount(ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cardRepository).findByAccountId(ACCOUNT_ID);
    }

    @Test
    void addCardToAccount_Success() throws CardService.CardAlreadyExistsException {
        // Arrange
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(cardRepository.findByAccountIdAndCardNumber(ACCOUNT_ID, CARD_NUMBER)).thenReturn(null);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // Act
        Card result = cardService.addCardToAccount(ACCOUNT_ID, testCard);

        // Assert
        assertNotNull(result);
        assertEquals(CARD_NUMBER, result.getCardNumber());
        assertEquals(ACCOUNT_ID, result.getAccount().getId());
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(cardRepository).findByAccountIdAndCardNumber(ACCOUNT_ID, CARD_NUMBER);
        verify(cardRepository).save(testCard);
    }

    @Test
    void addCardToAccount_AccountNotFound_ThrowsException() {
        // Arrange
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            cardService.addCardToAccount(ACCOUNT_ID, testCard)
        );
        assertEquals("Cuenta no encontrada", exception.getMessage());
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void addCardToAccount_CardExists_ThrowsException() {
        // Arrange
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(cardRepository.findByAccountIdAndCardNumber(ACCOUNT_ID, CARD_NUMBER)).thenReturn(testCard);

        // Act & Assert
        assertThrows(CardService.CardAlreadyExistsException.class, () ->
            cardService.addCardToAccount(ACCOUNT_ID, testCard)
        );
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(cardRepository).findByAccountIdAndCardNumber(ACCOUNT_ID, CARD_NUMBER);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deleteCard_Success() {
        // Arrange
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(testCard));

        // Act
        cardService.deleteCard(ACCOUNT_ID, CARD_ID);

        // Assert
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository).delete(testCard);
    }

    @Test
    void deleteCard_CardNotFound_ThrowsException() {
        // Arrange
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            cardService.deleteCard(ACCOUNT_ID, CARD_ID)
        );
        assertEquals("Tarjeta no encontrada", exception.getMessage());
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).delete(any());
    }

    @Test
    void deleteCard_WrongAccount_ThrowsException() {
        // Arrange
        Long wrongAccountId = 999L;
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(testCard));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            cardService.deleteCard(wrongAccountId, CARD_ID)
        );
        assertEquals("La tarjeta no pertenece a la cuenta especificada", exception.getMessage());
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).delete(any());
    }
}