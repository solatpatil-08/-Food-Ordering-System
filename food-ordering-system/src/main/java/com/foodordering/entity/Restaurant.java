package com.foodordering.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, length = 400)
  private String address;

  @Column(nullable = false, length = 25)
  private String phone;

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "restaurant")
  private List<MenuItem> menuItems = new ArrayList<>();

  @OneToMany(mappedBy = "restaurant")
  private List<CustomerOrder> orders = new ArrayList<>();

  public Restaurant() {}

  public Restaurant(String n, String a, String p) {
    name = n;
    address = a;
    phone = p;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public String getPhone() {
    return phone;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean v) {
    active = v;
  }

  public String toString() {
    return "Restaurant #" + id + " | " + name + " | " + address + " | active=" + active;
  }
}
