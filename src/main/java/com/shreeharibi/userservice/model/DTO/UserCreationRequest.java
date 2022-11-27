package com.shreeharibi.userservice.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    private String firstname;

    private String lastname;

    private String email;

    private String password;
}
