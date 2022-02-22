package com.epam.esm;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Order  extends RepresentationModel<Order> {

    private long id;

    private long userId;

    private LocalDateTime orderDate;

    private double totalOrderAmount;

    private Set<GiftCertificate> giftCertificates = new HashSet<>();

    public Order() {
    }

    public Order(LocalDateTime orderDate, double totalOrderAmount, Set<GiftCertificate> giftCertificates) {
        this.orderDate = orderDate;
        this.totalOrderAmount = totalOrderAmount;
        this.giftCertificates = giftCertificates;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(double totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public Set<GiftCertificate> getGiftCertificates() {
        return giftCertificates;
    }

    public void setGiftCertificates(Set<GiftCertificate> giftCertificates) {
        this.giftCertificates = giftCertificates;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void updateTotalOrderAmount(double certificateCost){
        totalOrderAmount += certificateCost;
    }

}
