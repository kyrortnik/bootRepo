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

    //TODO JsonIgnore
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<GiftCertificate> certificates = new HashSet<>();

//    protected Tag() {
//    }
//
//    public Tag(String name) {
//        this.name = name;
//    }
//
//    public Tag(Long id, String name) {
//        this.id = id;
//        this.name = name;
//    }
//
//    public void addCertificate(GiftCertificate giftCertificate) {
//        this.certificates.add(giftCertificate);
//        giftCertificate.getTags().add(this);
//    }
//
//    public void removeCertificate(GiftCertificate giftCertificate) {
//        this.certificates.remove(giftCertificate);
//        giftCertificate.getTags().remove(this);
//    }
//
//    public Set<GiftCertificate> getCertificates() {
//        return certificates;
//    }
//
//    public void setCertificates(Set<GiftCertificate> certificates) {
//        this.certificates = certificates;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Tag tag = (Tag) o;
//
//        if (id != null ? !id.equals(tag.id) : tag.id != null) return false;
//        return name != null ? name.equals(tag.name) : tag.name == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (name != null ? name.hashCode() : 0);
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return "Tag{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                '}';
//    }
}
