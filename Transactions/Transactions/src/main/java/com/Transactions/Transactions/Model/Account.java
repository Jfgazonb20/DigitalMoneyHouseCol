package com.Transactions.Transactions.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cbu;

    @Column(nullable = false)
    private Double balance;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Referencia al ID del usuario en users-service
}
