package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.controller.DepositController.DepositRequest;
import com.digitalmoneyhouse.controller.TransferController.TransferRequest;
import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Transaction;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction testTransaction;
    private final Long SOURCE_ACCOUNT_ID = 1L;
    private final Long DESTINATION_ACCOUNT_ID = 2L;
    private final Long TRANSACTION_ID = 1L;
    private final Double INITIAL_BALANCE = 1000.0;
    private final Double TRANSFER_AMOUNT = 500.0;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sourceAccount = new Account();
        sourceAccount.setId(SOURCE_ACCOUNT_ID);
        sourceAccount.setBalance(INITIAL_BALANCE);

        destinationAccount = new Account();
        destinationAccount.setId(DESTINATION_ACCOUNT_ID);
        destinationAccount.setBalance(INITIAL_BALANCE);

        testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);
        testTransaction.setAccount(sourceAccount);
        testTransaction.setAmount(TRANSFER_AMOUNT);
        testTransaction.setType("transfer");
    }

    @Test
    void getTransactionsByAccount_ReturnsTransactionList() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAccountIdOrderByDateDesc(SOURCE_ACCOUNT_ID))
            .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByAccount(SOURCE_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository).findByAccountIdOrderByDateDesc(SOURCE_ACCOUNT_ID);
    }

    @Test
    void getTransactionDetail_ValidTransaction_ReturnsTransaction() {
        // Arrange
        when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));

        // Act
        Transaction result = transactionService.getTransactionDetail(SOURCE_ACCOUNT_ID, TRANSACTION_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TRANSACTION_ID, result.getId());
        verify(transactionRepository).findById(TRANSACTION_ID);
    }

    @Test
    void getTransactionDetail_WrongAccount_ReturnsNull() {
        // Arrange
        when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));

        // Act
        Transaction result = transactionService.getTransactionDetail(999L, TRANSACTION_ID);

        // Assert
        assertNull(result);
        verify(transactionRepository).findById(TRANSACTION_ID);
    }

    @Test
    void deposit_Success() {
        // Arrange
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(TRANSFER_AMOUNT);

        when(accountRepository.findById(SOURCE_ACCOUNT_ID)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Act
        transactionService.deposit(SOURCE_ACCOUNT_ID, depositRequest);

        // Assert
        verify(accountRepository).findById(SOURCE_ACCOUNT_ID);
        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("deposit", savedTransaction.getType());
        assertEquals(TRANSFER_AMOUNT, savedTransaction.getAmount());
        assertEquals(SOURCE_ACCOUNT_ID, savedTransaction.getAccount().getId());
    }

    @Test
    void deposit_AccountNotFound_ThrowsException() {
        // Arrange
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(TRANSFER_AMOUNT);

        when(accountRepository.findById(SOURCE_ACCOUNT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            transactionService.deposit(SOURCE_ACCOUNT_ID, depositRequest)
        );
        verify(accountRepository).findById(SOURCE_ACCOUNT_ID);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_Success() throws TransactionService.AccountNotFoundException,
                                 TransactionService.InsufficientFundsException {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(TRANSFER_AMOUNT);
        transferRequest.setDestinationAccountId(DESTINATION_ACCOUNT_ID);

        when(accountRepository.findById(SOURCE_ACCOUNT_ID)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(DESTINATION_ACCOUNT_ID)).thenReturn(Optional.of(destinationAccount));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Act
        transactionService.transfer(SOURCE_ACCOUNT_ID, transferRequest);

        // Assert
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(destinationAccount);
        verify(transactionRepository).save(transactionCaptor.capture());

        assertEquals(INITIAL_BALANCE - TRANSFER_AMOUNT, sourceAccount.getBalance());
        assertEquals(INITIAL_BALANCE + TRANSFER_AMOUNT, destinationAccount.getBalance());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("transfer", savedTransaction.getType());
        assertEquals(TRANSFER_AMOUNT, savedTransaction.getAmount());
        assertEquals(DESTINATION_ACCOUNT_ID, savedTransaction.getDestinationAccountId());
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(INITIAL_BALANCE + 100.0);
        transferRequest.setDestinationAccountId(DESTINATION_ACCOUNT_ID);

        when(accountRepository.findById(SOURCE_ACCOUNT_ID)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(DESTINATION_ACCOUNT_ID)).thenReturn(Optional.of(destinationAccount));

        // Act & Assert
        assertThrows(TransactionService.InsufficientFundsException.class, () ->
            transactionService.transfer(SOURCE_ACCOUNT_ID, transferRequest)
        );
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_AccountNotFound_ThrowsException() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(TRANSFER_AMOUNT);
        transferRequest.setDestinationAccountId(DESTINATION_ACCOUNT_ID);

        when(accountRepository.findById(SOURCE_ACCOUNT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionService.AccountNotFoundException.class, () ->
            transactionService.transfer(SOURCE_ACCOUNT_ID, transferRequest)
        );
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getRecentTransfers_ReturnsTopFiveTransfers() {
        // Arrange
        List<Transaction> allTransactions = Arrays.asList(
            createTransferTransaction(1L),
            createDepositTransaction(2L),
            createTransferTransaction(3L),
            createTransferTransaction(4L),
            createDepositTransaction(5L),
            createTransferTransaction(6L),
            createTransferTransaction(7L)
        );

        when(transactionRepository.findByAccountIdOrderByDateDesc(SOURCE_ACCOUNT_ID))
            .thenReturn(allTransactions);

        // Act
        List<Transaction> result = transactionService.getRecentTransfers(SOURCE_ACCOUNT_ID);

        // Assert
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(t -> "transfer".equalsIgnoreCase(t.getType())));
        verify(transactionRepository).findByAccountIdOrderByDateDesc(SOURCE_ACCOUNT_ID);
    }

    private Transaction createTransferTransaction(Long id) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setType("transfer");
        return t;
    }

    private Transaction createDepositTransaction(Long id) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setType("deposit");
        return t;
    }
}