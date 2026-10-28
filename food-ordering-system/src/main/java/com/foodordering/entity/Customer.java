package com.foodordering.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "customers")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 160)
  private String email;

  @Column(nullable = false, length = 25)
  private String phone;

  @Column(nullable = false, length = 500)
  private String address;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  private ShoppingCart cart;

  @OneToMany(mappedBy = "customer")
  private List<CustomerOrder> orders = new ArrayList<>();

  public Customer() {}

  public Customer(String n, String e, String p, String a) {
    name = n;
    email = e;
    phone = p;
    address = a;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String v) {
    name = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String v) {
    phone = v;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String v) {
    address = v;
  }

  public ShoppingCart getCart() {
    return cart;
  }

  public void setCart(ShoppingCart v) {
    cart = v;
  }

  public String toString() {
    return "Customer #" + id + " | " + name + " | " + email + " | " + phone + " | " + address;
  }
}
