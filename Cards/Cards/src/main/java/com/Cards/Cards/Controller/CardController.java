package com.Cards.Cards.Controller;

import com.Cards.Cards.Exception.BadRequestException;
import com.Cards.Cards.Model.Card;
import com.Cards.Cards.Service.CardService;
import com.Cards.Cards.Security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private JwtUtil jwtUtil;

    // Método privado para validar permisos
    private boolean isAuthorized(String token, Long accountId) {
        String jwtToken = token.substring(7); // Eliminar "Bearer "
        String userIdFromToken = jwtUtil.extractUserId(jwtToken);
        return userIdFromToken.equals(accountId.toString());
    }

    // Obtener todas las tarjetas
    @GetMapping
    public ResponseEntity<?> getAllCards(@PathVariable Long accountId,
                                         @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para acceder a este recurso.");
        }

        List<Card> cards = cardService.getAllCards(accountId);
        return ResponseEntity.ok(cards);
    }

    // Obtener una tarjeta específica
    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long accountId,
                                         @PathVariable Long cardId,
                                         @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para acceder a este recurso.");
        }

        Card card = cardService.getCardById(accountId, cardId);
        return ResponseEntity.ok(card);
    }

    // Agregar una tarjeta
    @PostMapping
    public ResponseEntity<?> addCard(@PathVariable Long accountId,
                                     @RequestBody Card card,
                                     @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para acceder a este recurso.");
        }

        if (card.getCardNumber() == null || card.getCardType() == null) {
            throw new BadRequestException("El número de tarjeta y el tipo son obligatorios.");
        }

        card.setAccountId(accountId);
        Card savedCard = cardService.saveCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    // Eliminar una tarjeta
    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long accountId,
                                        @PathVariable Long cardId,
                                        @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para acceder a este recurso.");
        }

        cardService.deleteCard(accountId, cardId);
        return ResponseEntity.ok("Tarjeta eliminada correctamente.");
    }
}
