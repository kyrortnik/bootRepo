package com.epam.esm.dto;


//import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Component
public class LoginDto {


    @NonNull
    private  String username;

    @NonNull
    private  String password;

    private String firstName;

    private String lastName;

}
