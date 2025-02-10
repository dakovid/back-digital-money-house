package com.digitalmoneyhouse.controller;

import com.digitalmoneyhouse.model.Transaction;
import com.digitalmoneyhouse.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}/activity")
public class ActivityController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getActivity(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(@PathVariable Long accountId, @PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionDetail(accountId, transactionId);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.status(404).body("Transacción no encontrada.");
        }
    }
}

