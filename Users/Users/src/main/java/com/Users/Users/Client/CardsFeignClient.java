package com.Users.Users.Client;

import com.Users.Users.DTO.CardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cards-service", url = "http://localhost:8082/api/cards")
public interface CardsFeignClient {

    @GetMapping("/{userId}")
    List<CardResponse> getCardsByUserId(@PathVariable Long userId);
}
