package com.foodordering.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_reference", nullable = false, unique = true, length = 40)
  private String orderReference;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @Column(nullable = false)
  private LocalDateTime orderDate = LocalDateTime.now();

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrderStatus status = OrderStatus.PLACED;

  @Column(nullable = false, length = 500)
  private String deliveryAddress;

  @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  public CustomerOrder() {}

  public CustomerOrder(String ref, Customer c, Restaurant r, BigDecimal total, String address) {
    orderReference = ref;
    customer = c;
    restaurant = r;
    totalAmount = total;
    deliveryAddress = address;
  }

  public Long getId() {
    return id;
  }

  public String getOrderReference() {
    return orderReference;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus v) {
    status = v;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void add(OrderItem i) {
    items.add(i);
    i.setCustomerOrder(this);
  }

  public String toString() {
    return "Order #"
        + id
        + " ["
        + orderReference
        + "] | "
        + customer.getName()
        + " | "
        + restaurant.getName()
        + " | Rs."
        + totalAmount
        + " | "
        + status
        + " | "
        + orderDate;
  }
}
