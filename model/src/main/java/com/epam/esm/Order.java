package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Table(name = "orders")
public class Order extends RepresentationModel<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_cost")
    private double orderCost;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_certificate_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GiftCertificate giftCertificate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;


    public Order(GiftCertificate giftCertificate, User user) {
        this.giftCertificate = giftCertificate;
        this.user = user;
    }


}
