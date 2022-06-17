package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@Audited
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    //TODO -- do i need this annotation?
    private String name;

    private String description;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users;

    @Override
    public String getAuthority() {
        return name;
    }
}
