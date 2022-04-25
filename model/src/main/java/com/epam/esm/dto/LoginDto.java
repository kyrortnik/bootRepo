package com.epam.esm.dto;

import lombok.*;
import org.springframework.stereotype.Component;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class LoginDto {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

}
