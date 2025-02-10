package com.digitalmoneyhouse.controller;

import com.digitalmoneyhouse.model.Card;
import com.digitalmoneyhouse.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{accountId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<?> getCards(@PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.getCardsByAccount(accountId));
    }

    @PostMapping
    public ResponseEntity<?> registerCard(@PathVariable Long accountId, @RequestBody Card card) {
        try {
            Card savedCard = cardService.addCardToAccount(accountId, card);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
        } catch (CardService.CardAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tarjeta ya registrada.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos inválidos.");
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        try {
            cardService.deleteCard(accountId, cardId);
            return ResponseEntity.ok("Tarjeta eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al eliminar tarjeta: " + e.getMessage());
        }
    }
}

