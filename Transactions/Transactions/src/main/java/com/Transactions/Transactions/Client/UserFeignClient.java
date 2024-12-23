package com.Transactions.Transactions.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service", url = "http://localhost:8081/api/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/cvu/{cvu}")
    UserResponse getUserByCvu(@PathVariable("cvu") String cvu);
}
