package com.epam.esm;

//import com.epam.esm.listeners.AuditListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//@EntityListeners(AuditListener.class)
@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@Component
@Table(name = "certificates")
public class GiftCertificate extends RepresentationModel<GiftCertificate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long price;

    private Long duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "certificates_tags",
            joinColumns = {@JoinColumn(name = "certificate_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
//    @JsonIgnore
    private Set<Tag> tags = new HashSet<>();


    @OneToMany(mappedBy = "giftCertificate", orphanRemoval = true)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();



    public void addTag(Tag tag) {
        tags.add(tag);
        tag.addCertificate(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.removeCertificate(this);
    }

}
