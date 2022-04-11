package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Component
@Table(name = "tags")
public class Tag extends RepresentationModel<Tag> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;


    @ManyToMany(mappedBy = "tags")
//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "certificates_tags",
//            joinColumns = {@JoinColumn(name = "tag_id")},
//            inverseJoinColumns = {@JoinColumn(name = "certificate_id")}
//    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<GiftCertificate> certificates = new HashSet<>();


    public Tag(Long id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(@NonNull String name) {
        this.name = name;
    }


}
