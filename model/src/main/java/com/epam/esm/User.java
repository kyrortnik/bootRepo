package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
@Entity
@Data
@NoArgsConstructor
@Component
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    private String username;

    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",
                    referencedColumnName = "id"))
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Order> orders = new HashSet<>();

    public User(UserBuilder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.firstName = builder.firstName;
        this.secondName = builder.secondName;
        this.roles = builder.roles;
    }


    public static class UserBuilder {

        private long id;
        private final String username;
        private final String password;
        private String firstName;
        private String secondName;
        private List<Role> roles;

        public UserBuilder(String username, String password){
            this.username = username;
            this.password = password;
        }

        public UserBuilder id (long id){
            this.id = id;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder secondName(String secondName) {
            this.secondName = secondName;
            return this;
        }
        public UserBuilder role(Role role){
            roles = Arrays.asList(role);
            return this;
        }

        public User build() {
            return new User(this);

        }
    }
}
