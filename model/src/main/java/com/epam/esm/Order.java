package com.epam.esm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false, exclude = {"user", "giftCertificate"})
@Data
@Component
@Entity
//@DynamicUpdate
@Table(name = "orders")
public class Order extends RepresentationModel<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_cost")
    private double orderCost;

    //TODO -- CascadeType, nullable
    @ManyToOne
    @JoinColumn(name = "gift_certificate_id", nullable = false)
    private GiftCertificate giftCertificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
