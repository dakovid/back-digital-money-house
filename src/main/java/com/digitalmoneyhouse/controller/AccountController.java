package com.digitalmoneyhouse.controller;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Obtener saldo e información de cuenta
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.status(404).body("Cuenta no encontrada.");
        }
    }

    // Actualizar información de la cuenta
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        try {
            Account updatedAccount = accountService.updateAccount(id, account);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al actualizar cuenta: " + e.getMessage());
        }
    }

    // Dashboard: últimos 5 movimientos
    @GetMapping("/{id}/transactions")
    public ResponseEntity<?> getLastTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getLastTransactions(id));
    }
}

