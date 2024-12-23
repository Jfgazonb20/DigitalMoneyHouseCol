package com.Cards.Cards.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cards")
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId; // Relación con la cuenta

    private String cardNumber;
    private String cardType; // CREDIT or DEBIT
    private String holderName;
    private String expirationDate;
}
