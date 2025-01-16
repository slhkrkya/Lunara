package com.example.Lunara.messaging_service.client;

import com.example.Lunara.messaging_service.DTO.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-notification", url = "http://localhost:8081/api/users")
public interface UserClient {
    @GetMapping("/id/{id}")
    User getUserById(@PathVariable("id") long id);
}