package com.Cards.Cards.Service;

import com.Cards.Cards.Exception.ConflictException;
import com.Cards.Cards.Exception.ResourceNotFoundException;
import com.Cards.Cards.Model.Card;
import com.Cards.Cards.Repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getAllCards(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    public Card getCardById(Long accountId, Long cardId) {
        return cardRepository.findById(cardId)
                .filter(card -> card.getAccountId().equals(accountId))
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada para la cuenta proporcionada."));
    }

    public Card saveCard(Card card) {
        boolean cardExists = cardRepository.findByAccountId(card.getAccountId())
                .stream()
                .anyMatch(existing -> existing.getCardNumber().equals(card.getCardNumber()));

        if (cardExists) {
            throw new ConflictException("La tarjeta ya está asociada a esta cuenta.");
        }
        return cardRepository.save(card);
    }

    public void deleteCard(Long accountId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .filter(c -> c.getAccountId().equals(accountId))
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada para esta cuenta."));
        cardRepository.delete(card);
    }
}
