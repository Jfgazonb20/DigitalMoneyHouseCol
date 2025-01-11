package com.Cards.Cards.Service;

import com.Cards.Cards.Exception.ConflictException;
import com.Cards.Cards.Exception.ResourceNotFoundException;
import com.Cards.Cards.Model.Card;
import com.Cards.Cards.Repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        card = new Card();
        card.setId(1L);
        card.setAccountId(1L);
        card.setCardNumber("1234567812345678");
        card.setCardType("CREDIT");
        card.setHolderName("John Doe");
        card.setExpirationDate("12/25");
    }

    @Test
    void getAllCards_Success() {
        when(cardRepository.findByAccountId(1L)).thenReturn(List.of(card));

        List<Card> cards = cardService.getAllCards(1L);

        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals("1234567812345678", cards.get(0).getCardNumber());
    }

    @Test
    void getCardById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Card result = cardService.getCardById(1L, 1L);

        assertNotNull(result);
        assertEquals("1234567812345678", result.getCardNumber());
    }

    @Test
    void getCardById_NotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                cardService.getCardById(1L, 1L));

        assertEquals("Tarjeta no encontrada para la cuenta proporcionada.", exception.getMessage());
    }

    @Test
    void saveCard_Success() {
        when(cardRepository.findByAccountId(1L)).thenReturn(List.of());
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card savedCard = cardService.saveCard(card);

        assertNotNull(savedCard);
        assertEquals("1234567812345678", savedCard.getCardNumber());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void saveCard_Conflict() {
        when(cardRepository.findByAccountId(1L)).thenReturn(List.of(card));

        Exception exception = assertThrows(ConflictException.class, () ->
                cardService.saveCard(card));

        assertEquals("La tarjeta ya está asociada a esta cuenta.", exception.getMessage());
    }

    @Test
    void deleteCard_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L, 1L);

        verify(cardRepository, times(1)).delete(any(Card.class));
    }

    @Test
    void deleteCard_NotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                cardService.deleteCard(1L, 1L));

        assertEquals("Tarjeta no encontrada para esta cuenta.", exception.getMessage());
    }
}
