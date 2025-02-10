package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Transaction;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccount_ExistingAccount_ReturnsAccount() {
        // Arrange
        Long accountId = 1L;
        Account expectedAccount = new Account();
        expectedAccount.setId(accountId);
        expectedAccount.setBalance(1000.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        // Act
        Account result = accountService.getAccount(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(1000.0, result.getBalance());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccount_NonExistingAccount_ReturnsNull() {
        // Arrange
        Long accountId = 999L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act
        Account result = accountService.getAccount(accountId);

        // Assert
        assertNull(result);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void updateAccount_ExistingAccount_UpdatesBalance() {
        // Arrange
        Long accountId = 1L;
        Account existingAccount = new Account();
        existingAccount.setId(accountId);
        existingAccount.setBalance(1000.0);

        Account updatedData = new Account();
        updatedData.setBalance(2000.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        // Act
        Account result = accountService.updateAccount(accountId, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals(2000.0, result.getBalance());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(existingAccount);
    }

    @Test
    void updateAccount_NonExistingAccount_ThrowsException() {
        // Arrange
        Long accountId = 999L;
        Account updatedData = new Account();
        updatedData.setBalance(2000.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            accountService.updateAccount(accountId, updatedData)
        );
        assertEquals("Cuenta no encontrada", exception.getMessage());
        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getLastTransactions_ReturnsTopFiveTransactions() {
        // Arrange
        Long accountId = 1L;
        List<Transaction> expectedTransactions = Arrays.asList(
            new Transaction(),
            new Transaction(),
            new Transaction(),
            new Transaction(),
            new Transaction()
        );

        when(transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = accountService.getLastTransactions(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(transactionRepository).findTop5ByAccountIdOrderByDateDesc(accountId);
    }
}