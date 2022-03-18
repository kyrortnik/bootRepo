package com.epam.esm;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


//@EntityListeners(AuditListener.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class User extends RepresentationModel<User> {

    @Id
//    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
//    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Order> orders = new HashSet<>();


    public User(long id, String firstName, String secondName) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
    }
}
