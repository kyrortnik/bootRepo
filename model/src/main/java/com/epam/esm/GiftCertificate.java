package com.epam.esm;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Audited
@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@Component
@Table(name = "certificates")
public class GiftCertificate extends RepresentationModel<GiftCertificate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
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

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(
            name = "certificates_tags",
            joinColumns = {@JoinColumn(name = "certificate_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();


    @OneToMany(mappedBy = "giftCertificate", orphanRemoval = true)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    public GiftCertificate(Long id, @NonNull String name, String description, Long price, Long duration, LocalDateTime createDate, LocalDateTime lastUpdateDate){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public GiftCertificate(Long id, @NonNull String name, String description, Long price, Long duration, LocalDateTime createDate, LocalDateTime lastUpdateDate, Set<Tag> tags){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }

    public GiftCertificate( @NonNull String name, String description, Long price, Long duration, LocalDateTime createDate, LocalDateTime lastUpdateDate, Set<Tag> tags){
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }

    public GiftCertificate(@NonNull String name) {
        this.name = name;
    }




//    public void addTag(Tag tag) {
//        tags.add(tag);
//        tag.addCertificate(this);
//    }
//
//    public void removeTag(Tag tag) {
//        tags.remove(tag);
//        tag.removeCertificate(this);
//    }

//    public GiftCertificate(GiftCertificate existingGiftCertificate, GiftCertificate changedGiftCertificate) {
//        existingGiftCertificate.setDescription(nonNull(changedGiftCertificate.getDescription()) ? changedGiftCertificate.getDescription() : existingGiftCertificate.getDescription());
//        existingGiftCertificate.setPrice(nonNull(changedGiftCertificate.getPrice()) ? changedGiftCertificate.getPrice() : existingGiftCertificate.getPrice());
//        existingGiftCertificate.setDuration(nonNull(changedGiftCertificate.getDuration()) ? changedGiftCertificate.getDuration() : existingGiftCertificate.getDuration());
//        existingGiftCertificate.setCreateDate(nonNull(changedGiftCertificate.getCreateDate()) ? changedGiftCertificate.getCreateDate() : existingGiftCertificate.getCreateDate());
//        existingGiftCertificate.setLastUpdateDate(changedGiftCertificate.getLastUpdateDate());
//        existingGiftCertificate.setTags(!changedGiftCertificate.getTags().isEmpty() ? changedGiftCertificate.getTags() : existingGiftCertificate.getTags());
//    }

}
