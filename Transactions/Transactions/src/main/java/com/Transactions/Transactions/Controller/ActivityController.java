package com.Transactions.Transactions.Controller;

import com.Transactions.Transactions.Exception.InsufficientFundsException;
import com.Transactions.Transactions.Model.Activity;
import com.Transactions.Transactions.Model.Account;
import com.Transactions.Transactions.Service.ActivityService;
import com.Transactions.Transactions.DTO.TransactionRequest;
import com.Transactions.Transactions.DTO.AccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions/accounts")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // Obtener todas las actividades
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getActivities(@PathVariable Long id, HttpServletRequest request) {
        String userIdFromToken = (String) request.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a esta cuenta.");
        }

        List<Activity> activities = activityService.getActivitiesForAccount(id);
        return ResponseEntity.ok(activities);
    }

    // Obtener detalles de una actividad específica
    @GetMapping("/{id}/activity/{transferenceId}")
    public ResponseEntity<?> getActivityDetails(
            @PathVariable Long id,
            @PathVariable Long transferenceId,
            HttpServletRequest request) {

        String userIdFromToken = (String) request.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a esta actividad.");
        }

        Activity activity = activityService.getActivityDetails(id, transferenceId);
        return ResponseEntity.ok(activity);
    }

    // Consultar el saldo disponible
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id, HttpServletRequest request) {
        String userIdFromToken = (String) request.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a esta cuenta.");
        }

        // Obtener saldo desde el servicio
        double balance = activityService.getBalanceForAccount(id);

        return ResponseEntity.ok(Map.of("accountId", id, "balance", balance));
    }

    // Registrar un ingreso de dinero
    @PostMapping("/{id}/income")
    public ResponseEntity<?> registerIncome(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            HttpServletRequest httpRequest) {

        String userIdFromToken = (String) httpRequest.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para registrar ingresos en esta cuenta.");
        }

        // Registrar la transacción en la base de datos
        Activity activity = activityService.registerIncome(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    // Registrar una transferencia de dinero
    @PostMapping("/{id}/transferences")
    public ResponseEntity<?> transferFunds(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            HttpServletRequest httpRequest) {

        String userIdFromToken = (String) httpRequest.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para transferir fondos desde esta cuenta.");
        }

        try {
            Activity activity = activityService.transferFunds(id, request);
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        }
    }

    // Crear una cuenta asociada a un usuario
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest request) {
        Account account = activityService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // Obtener una lista de destinatarios recientes
    @GetMapping("/{id}/transferences")
    public ResponseEntity<?> getRecentTransferences(@PathVariable Long id, HttpServletRequest request) {
        String userIdFromToken = (String) request.getAttribute("userId");

        if (!userIdFromToken.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a esta cuenta.");
        }

        List<Activity> recentTransferences = activityService.getRecentTransferencesForAccount(id);
        return ResponseEntity.ok(recentTransferences);
    }
}
