package com.Transactions.Transactions.Service;

import com.Transactions.Transactions.DTO.TransactionRequest;
import com.Transactions.Transactions.DTO.AccountRequest;
import com.Transactions.Transactions.Exception.ResourceNotFoundException;
import com.Transactions.Transactions.Exception.InsufficientFundsException;
import com.Transactions.Transactions.Model.Activity;
import com.Transactions.Transactions.Model.Account;
import com.Transactions.Transactions.Repository.ActivityRepository;
import com.Transactions.Transactions.Repository.AccountRepository;
import com.Transactions.Transactions.Client.UserFeignClient;
import com.Transactions.Transactions.Client.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private ActivityService activityService;

    private Account account;
    private Activity activity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setCbu("1234567890123456789012");
        account.setBalance(1000.0);
        account.setUserId(1L);

        activity = new Activity();
        activity.setId(1L);
        activity.setAccountId(1L);
        activity.setType("INGRESO");
        activity.setAmount(500.0);
        activity.setDate(LocalDateTime.now());
        activity.setDescription("Ingreso inicial");
    }

    @Test
    void getActivitiesForAccount_Success() {
        when(activityRepository.findByAccountIdOrderByDateDesc(1L)).thenReturn(List.of(activity));

        List<Activity> activities = activityService.getActivitiesForAccount(1L);

        assertNotNull(activities);
        assertEquals(1, activities.size());
    }

    @Test
    void getActivitiesForAccount_NotFound() {
        when(activityRepository.findByAccountIdOrderByDateDesc(1L)).thenReturn(List.of());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                activityService.getActivitiesForAccount(1L));

        assertEquals("No se encontraron actividades para la cuenta con ID: 1", exception.getMessage());
    }

    @Test
    void getActivityDetails_Success() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        Activity result = activityService.getActivityDetails(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getAccountId());
    }

    @Test
    void getActivityDetails_NotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                activityService.getActivityDetails(1L, 1L));

        assertEquals("Transacción no encontrada o no pertenece a la cuenta.", exception.getMessage());
    }

    @Test
    void registerIncome_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(500.0);
        request.setDescription("Ingreso de prueba");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity result = activityService.registerIncome(1L, request);

        assertNotNull(result);
        assertEquals("INGRESO", result.getType());
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void transferFunds_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(200.0);
        request.setCbuDestino("9876543210987654321098");

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setCbu("9876543210987654321098");
        destinationAccount.setBalance(500.0);

        UserResponse originUser = new UserResponse();
        originUser.setId(1L);
        originUser.setFullName("John Doe");

        UserResponse destinationUser = new UserResponse();
        destinationUser.setId(2L);
        destinationUser.setFullName("Jane Smith");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByCbu("9876543210987654321098")).thenReturn(Optional.of(destinationAccount));
        when(userFeignClient.getUserById(1L)).thenReturn(originUser);
        when(userFeignClient.getUserByCvu("9876543210987654321098")).thenReturn(destinationUser);

        Activity result = activityService.transferFunds(1L, request);

        assertNotNull(result);
        assertEquals("TRANSFERENCIA", result.getType());
        assertEquals(800.0, account.getBalance());
        assertEquals(700.0, destinationAccount.getBalance());
    }

    @Test
    void transferFunds_InsufficientFunds() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(2000.0);
        request.setCbuDestino("9876543210987654321098");

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setCbu("9876543210987654321098");
        destinationAccount.setBalance(500.0);

        UserResponse originUser = new UserResponse();
        originUser.setId(1L);
        originUser.setFullName("John Doe");

        UserResponse destinationUser = new UserResponse();
        destinationUser.setId(2L);
        destinationUser.setFullName("Jane Smith");

        // Configurar mocks para evitar excepciones no relacionadas
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByCbu("9876543210987654321098")).thenReturn(Optional.of(destinationAccount));
        when(userFeignClient.getUserById(1L)).thenReturn(originUser);
        when(userFeignClient.getUserByCvu("9876543210987654321098")).thenReturn(destinationUser);

        // Ejecutar y verificar que la excepción lanzada sea por fondos insuficientes
        Exception exception = assertThrows(InsufficientFundsException.class, () ->
                activityService.transferFunds(1L, request));

        assertEquals("Fondos insuficientes para realizar la transferencia.", exception.getMessage());
    }


    @Test
    void createAccount_Success() {
        AccountRequest request = new AccountRequest(1L, "1234567890123456789012", 0.0);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = activityService.createAccount(request);

        assertNotNull(result);
        assertEquals("1234567890123456789012", result.getCbu());
    }
}
