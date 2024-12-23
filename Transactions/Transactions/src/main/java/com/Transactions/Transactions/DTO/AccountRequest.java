package com.Transactions.Transactions.DTO;

public class AccountRequest {

    private Long userId; // ID del usuario asociado
    private String cbu;  // Clave Bancaria Uniforme (único para cada cuenta)
    private Double initialBalance; // Saldo inicial de la cuenta

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
