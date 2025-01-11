package com.Transactions.Transactions.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId; // ID de la cuenta asociada a esta actividad
    private String type; // INGRESO o TRANSFERENCIA
    private Double amount; // Monto de la actividad
    private LocalDateTime date; // Fecha y hora de la actividad
    private String description; // Descripción opcional
    private String destinationCbu; // CVU de la cuenta destino (solo para transferencias)
}
