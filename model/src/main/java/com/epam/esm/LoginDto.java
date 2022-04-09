package com.epam.esm;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Data
public class LoginDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String firstName;

    private String lastName;

}
