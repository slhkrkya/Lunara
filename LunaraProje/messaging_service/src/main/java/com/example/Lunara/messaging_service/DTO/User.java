package com.example.Lunara.messaging_service.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class User {
    private long id;
    private String username;
    private String email;
    private String password;

}
