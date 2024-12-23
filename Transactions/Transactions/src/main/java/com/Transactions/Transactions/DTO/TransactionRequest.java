package com.Transactions.Transactions.DTO;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long cardId;
    private Double amount;
    private String description;
    private String cbuDestino;

}
