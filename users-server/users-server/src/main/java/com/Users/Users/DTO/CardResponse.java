package com.Users.Users.DTO;

public class CardResponse {
    private Long id;
    private String cardNumber;
    private String cardType;

    // Constructor
    public CardResponse(Long id, String cardNumber, String cardType) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
