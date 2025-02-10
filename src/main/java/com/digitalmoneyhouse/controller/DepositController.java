package com.digitalmoneyhouse.controller;

import com.digitalmoneyhouse.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}")
public class DepositController {

    @Autowired
    private TransactionService transactionService;

    // Ingreso de dinero desde tarjeta
    // POST /accounts/{accountId}/transferences
    @PostMapping("/deposits")
    public ResponseEntity<?> depositMoney(@PathVariable Long accountId, @RequestBody DepositRequest depositRequest) {
        try {
            transactionService.deposit(accountId, depositRequest);
            return ResponseEntity.ok("Depósito exitoso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Error en el depósito: " + e.getMessage());
        }
    }

    public static class DepositRequest {
        private Long cardId;
        private Double amount;
        // Getters y setters
        public Long getCardId() {
            return cardId;
        }
        public void setCardId(Long cardId) {
            this.cardId = cardId;
        }
        public Double getAmount() {
            return amount;
        }
        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}

