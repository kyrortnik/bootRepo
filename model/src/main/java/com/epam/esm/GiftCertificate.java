package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
@Entity
@Data
@NoArgsConstructor
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "certificates_tags",
            joinColumns = {@JoinColumn(name = "certificate_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private Set<Tag> tags = new HashSet<>();

    //TODO -- add HATEOAS to certificates getters
    @OneToMany(mappedBy = "giftCertificate", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    public GiftCertificate(GiftCertificateBuilder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.duration = builder.duration;
        this.createDate = builder.createDate;
        this.lastUpdateDate = builder.lastUpdateDate;
        this.tags = builder.tags;
        this.orders = builder.orders;

    }
    //TODO -- investigate why can't add commented line - HibernateException
    //Hibernate - A collection with cascade=”all-delete-orphan” was no longer referenced by the owning entity instance
    public void mergeTwoGiftCertificate(GiftCertificate changedGiftCertificate) {

        this.setName(nonNull(changedGiftCertificate.getName()) ? changedGiftCertificate.getName() : this.getName());
        this.setDescription(nonNull(changedGiftCertificate.getDescription()) ? changedGiftCertificate.getDescription() : this.getDescription());
        this.setPrice(nonNull(changedGiftCertificate.getPrice()) ? changedGiftCertificate.getPrice() : this.getPrice());
        this.setDuration(nonNull(changedGiftCertificate.getDuration()) ? changedGiftCertificate.getDuration() : this.getDuration());
        this.setCreateDate(nonNull(changedGiftCertificate.getCreateDate()) ? changedGiftCertificate.getCreateDate() : this.getCreateDate());
        this.setLastUpdateDate(changedGiftCertificate.getLastUpdateDate());
        this.setTags(nonNull(changedGiftCertificate.getTags()) ? changedGiftCertificate.getTags() : this.getTags());
//        this.setOrders(nonNull(changedGiftCertificate.getOrders()) ? changedGiftCertificate.getOrders() : this.getOrders());

    }

    public static class GiftCertificateBuilder {

        private  long id;
        private final String name;
        private  String description;
        private  long price;
        private  long duration;
        private  LocalDateTime createDate;
        private  LocalDateTime lastUpdateDate;
        private  Set<Tag> tags;
        private  Set<Order> orders;

        public GiftCertificateBuilder(String name) {
            this.name = name;

        }

        public GiftCertificateBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GiftCertificateBuilder description(String description) {
            this.description = description;
            return this;
        }
        public GiftCertificateBuilder price(long price) {
            this.price = price;
            return this;
        }
        public GiftCertificateBuilder duration(long duration) {
            this.duration = duration;
            return this;
        }
        public GiftCertificateBuilder createDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public GiftCertificateBuilder lastUpdateDate(LocalDateTime lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public GiftCertificateBuilder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public GiftCertificateBuilder orders(Set<Order> orders) {
            this.orders = orders;
            return this;
        }

        public GiftCertificate build() {
            return new GiftCertificate(this);
        }
    }

}
