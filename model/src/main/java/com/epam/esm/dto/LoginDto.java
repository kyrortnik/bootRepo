package com.epam.esm.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@Component
public class LoginDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String firstName;

    private String lastName;

}
