package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.Transaction;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Account updateAccount(Long accountId, Account accountData) {
        Account account = accountRepository.findById(accountId)
                          .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        if (accountData.getBalance() != null)
            account.setBalance(accountData.getBalance());
        return accountRepository.save(account);
    }

    public List<Transaction> getLastTransactions(Long accountId) {
        return transactionRepository.findTop5ByAccountIdOrderByDateDesc(accountId);
    }
}

