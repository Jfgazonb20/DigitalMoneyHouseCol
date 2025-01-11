package com.Users.Users.DTO;

public class AccountRequest {

    private Long userId;
    private String cbu;
    private Double initialBalance;

    // Constructor vacío
    public AccountRequest() {
    }

    // Constructor con parámetros
    public AccountRequest(Long userId, String cbu, Double initialBalance) {
        this.userId = userId;
        this.cbu = cbu;
        this.initialBalance = initialBalance;
    }

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    @Override
    public String toString() {
        return "AccountRequest{" +
                "userId=" + userId +
                ", cbu='" + cbu + '\'' +
                ", initialBalance=" + initialBalance +
                '}';
    }
}
