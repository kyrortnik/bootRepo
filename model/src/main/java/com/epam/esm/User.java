package com.epam.esm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false,exclude = "orders")
@Data
@Component
@Entity
@DynamicUpdate
@Table(name = "users")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;


//    @OneToMany(mappedBy = "user")
//    @JoinColumn(name = "order_")
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @ToString.Exclude
    Set<Order> orders = new HashSet<>();


}
