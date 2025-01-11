package com.Users.Users.Client;

import com.Users.Users.DTO.AccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transactions-service", url = "http://localhost:8083/api/transactions")
public interface TransactionsFeignClient {
    @PostMapping("/accounts")
    void createAccount(@RequestBody AccountRequest request);
}
