package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
@Component
@Entity
@Table(name ="tags")
public class Tag  extends RepresentationModel<Tag> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<GiftCertificate> certificates = new HashSet<>();


    public void addCertificate(GiftCertificate giftCertificate) {
        this.certificates.add(giftCertificate);
        giftCertificate.getTags().add(this);
    }

    public void removeCertificate(GiftCertificate giftCertificate) {
        this.certificates.remove(giftCertificate);
        giftCertificate.getTags().remove(this);
    }


}
