package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Entity
//@DynamicUpdate
@EqualsAndHashCode(callSuper = false/*,exclude = "orders"*/)
@Table(name = "users")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    /*@ToString.Exclude*/ Set<Order> orders = new HashSet<>();


}
