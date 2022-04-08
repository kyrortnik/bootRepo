package com.epam.esm;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Audited
@Data
@Entity
@Table(name = "auth_user_group")
public class AuthGroup /*implements GrantedAuthority*/ {

    @Id
    @Column(name = "auth_user_group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private  String username;

    @Column(name = "auth_group")
    private String authGroup;
}
