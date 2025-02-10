package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.controller.DepositController.DepositRequest;
import com.digitalmoneyhouse.controller.TransferController.TransferRequest;
import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Transaction;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByDateDesc(accountId);
    }

    public Transaction getTransactionDetail(Long accountId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction != null && transaction.getAccount().getId().equals(accountId)) {
            return transaction;
        }
        return null;
    }

    public void deposit(Long accountId, DepositRequest depositRequest) {
        Account account = accountRepository.findById(accountId)
                          .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        account.setBalance(account.getBalance() + depositRequest.getAmount());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(depositRequest.getAmount());
        transaction.setDate(new Date());
        transaction.setType("deposit");
        transactionRepository.save(transaction);
    }

    public void transfer(Long accountId, TransferRequest transferRequest) throws AccountNotFoundException, InsufficientFundsException {
        Account sourceAccount = accountRepository.findById(accountId)
                                .orElseThrow(() -> new AccountNotFoundException());
        Account destinationAccount = accountRepository.findById(transferRequest.getDestinationAccountId())
                                    .orElseThrow(() -> new AccountNotFoundException());

        if (sourceAccount.getBalance() < transferRequest.getAmount()) {
            throw new InsufficientFundsException();
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - transferRequest.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + transferRequest.getAmount());

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction();
        transaction.setAccount(sourceAccount);
        transaction.setAmount(transferRequest.getAmount());
        transaction.setDate(new Date());
        transaction.setType("transfer");
        transaction.setDestinationAccountId(destinationAccount.getId());
        transactionRepository.save(transaction);
    }

    public List<Transaction> getRecentTransfers(Long accountId) {
        List<Transaction> allTransactions = transactionRepository.findByAccountIdOrderByDateDesc(accountId);
        return allTransactions.stream()
                .filter(t -> "transfer".equalsIgnoreCase(t.getType()))
                .limit(5)
                .toList();
    }

    public static class AccountNotFoundException extends Exception { }
    public static class InsufficientFundsException extends Exception { }
}

