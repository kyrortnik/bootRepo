package com.epam.esm;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Data
@Audited
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    private String description;

    @Override
    public String getAuthority() {
        return name;
    }
}
