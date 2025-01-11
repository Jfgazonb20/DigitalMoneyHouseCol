package com.Transactions.Transactions.Service;

import com.Transactions.Transactions.DTO.TransactionRequest;
import com.Transactions.Transactions.Client.UserResponse;
import com.Transactions.Transactions.DTO.AccountRequest;
import com.Transactions.Transactions.Exception.ResourceNotFoundException;
import com.Transactions.Transactions.Exception.InsufficientFundsException;
import com.Transactions.Transactions.Model.Activity;
import com.Transactions.Transactions.Model.Account;
import com.Transactions.Transactions.Repository.ActivityRepository;
import com.Transactions.Transactions.Repository.AccountRepository;
import com.Transactions.Transactions.Client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    // Obtener actividades de una cuenta específica
    public List<Activity> getActivitiesForAccount(Long accountId) {
        List<Activity> activities = activityRepository.findByAccountIdOrderByDateDesc(accountId);
        if (activities.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron actividades para la cuenta con ID: " + accountId);
        }
        return activities;
    }

    // Obtener detalles de una actividad específica
    public Activity getActivityDetails(Long accountId, Long transferenceId) {
        return activityRepository.findById(transferenceId)
                .filter(activity -> activity.getAccountId().equals(accountId))
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada o no pertenece a la cuenta."));
    }

    // Consultar transferencias recientes
    public List<Activity> getRecentTransferencesForAccount(Long accountId) {
        List<Activity> transferences = activityRepository.findByAccountIdAndTypeOrderByDateDesc(accountId, "TRANSFERENCIA");
        if (transferences.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron transferencias recientes para la cuenta con ID: " + accountId);
        }
        return transferences;
    }

    // Consultar saldo de una cuenta
    public double getBalanceForAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + accountId));
        return account.getBalance();
    }

    // Registrar un ingreso de dinero
    public Activity registerIncome(Long accountId, TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + accountId));

        // Actualizar el saldo de la cuenta
        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);

        // Registrar la actividad
        Activity activity = new Activity();
        activity.setAccountId(accountId);
        activity.setType("INGRESO");
        activity.setAmount(request.getAmount());
        activity.setDate(LocalDateTime.now());
        activity.setDescription(request.getDescription());

        return activityRepository.save(activity);
    }

    // Transferir fondos entre cuentas
    public Activity transferFunds(Long accountId, TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        // Verificar cuenta origen
        Account originAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta origen no encontrada."));

        // Verificar usuario asociado a la cuenta origen
        UserResponse originUser = userFeignClient.getUserById(accountId);
        if (originUser == null) {
            throw new ResourceNotFoundException("Usuario asociado a la cuenta origen no encontrado.");
        }

        // Verificar cuenta destino
        UserResponse destinationUser = userFeignClient.getUserByCvu(request.getCbuDestino());
        if (destinationUser == null) {
            throw new ResourceNotFoundException("Cuenta destino no encontrada para el CBU: " + request.getCbuDestino());
        }

        Account destinationAccount = accountRepository.findByCbu(request.getCbuDestino())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada."));

        // Verificar fondos suficientes en la cuenta origen
        if (originAccount.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Fondos insuficientes para realizar la transferencia.");
        }

        // Actualizar saldo de la cuenta origen
        originAccount.setBalance(originAccount.getBalance() - request.getAmount());
        accountRepository.save(originAccount);

        // Actualizar saldo de la cuenta destino
        destinationAccount.setBalance(destinationAccount.getBalance() + request.getAmount());
        accountRepository.save(destinationAccount);

        // Registrar actividad en la cuenta origen
        Activity originActivity = new Activity();
        originActivity.setAccountId(accountId);
        originActivity.setType("TRANSFERENCIA");
        originActivity.setAmount(-request.getAmount());
        originActivity.setDate(LocalDateTime.now());
        originActivity.setDescription("Transferencia a CBU: " + request.getCbuDestino());
        originActivity.setDestinationCbu(request.getCbuDestino());
        activityRepository.save(originActivity);

        // Registrar actividad en la cuenta destino
        Activity destinationActivity = new Activity();
        destinationActivity.setAccountId(destinationAccount.getId());
        destinationActivity.setType("INGRESO");
        destinationActivity.setAmount(request.getAmount());
        destinationActivity.setDate(LocalDateTime.now());
        destinationActivity.setDescription("Transferencia recibida de cuenta ID: " + accountId);
        destinationActivity.setDestinationCbu(request.getCbuDestino());
        activityRepository.save(destinationActivity);

        return originActivity;
    }

    // Crear cuenta asociada a un usuario
    public Account createAccount(AccountRequest request) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setCbu(request.getCbu());
        account.setBalance(request.getInitialBalance());

        return accountRepository.save(account);
    }
}
