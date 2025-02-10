package com.digitalmoneyhouse.controller;

import com.digitalmoneyhouse.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}/transferences")
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    // Listar destinatarios recientes (últimas transferencias)
    @GetMapping
    public ResponseEntity<?> getRecentTransfers(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getRecentTransfers(accountId));
    }

    // Realizar una transferencia
    @PostMapping
    public ResponseEntity<?> transferMoney(@PathVariable Long accountId, @RequestBody TransferRequest transferRequest) {
        try {
            transactionService.transfer(accountId, transferRequest);
            return ResponseEntity.ok("Transferencia exitosa.");
        } catch (TransactionService.AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta inexistente.");
        } catch (TransactionService.InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.GONE).body("Fondos insuficientes.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error en la transferencia: " + e.getMessage());
        }
    }

    public static class TransferRequest {
        private Long destinationAccountId;
        private Double amount;
        // Getters y setters
        public Long getDestinationAccountId() {
            return destinationAccountId;
        }
        public void setDestinationAccountId(Long destinationAccountId) {
            this.destinationAccountId = destinationAccountId;
        }
        public Double getAmount() {
            return amount;
        }
        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}

