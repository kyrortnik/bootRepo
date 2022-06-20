package com.epam.esm;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Audited
@Entity
@EqualsAndHashCode
@Data
@NoArgsConstructor
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

    public Order(OrderBuilder builder) {
        this.id = builder.id;
        this.orderDate = builder.orderDate;
        this.orderCost = builder.orderCost;
        this.giftCertificate = builder.giftCertificate;
        this.user = builder.user;
    }


    public static class OrderBuilder {
        private long id;
        private LocalDateTime orderDate;
        private GiftCertificate giftCertificate;
        private User user;
        private double orderCost;



        public OrderBuilder id(long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderBuilder orderCost(double orderCost) {
            this.orderCost = orderCost;
            return this;
        }

        public OrderBuilder giftCertificate(GiftCertificate giftCertificate) {
            this.giftCertificate = giftCertificate;
            return this;
        }

        public OrderBuilder user(User user) {
            this.user = user;
            return this;
        }

        public Order build() {
            return new Order(this);
        }

    }

}
