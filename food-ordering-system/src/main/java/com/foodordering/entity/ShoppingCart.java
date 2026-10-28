package com.foodordering.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "customer_id", nullable = false, unique = true)
  private Customer customer;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items = new ArrayList<>();

  public ShoppingCart() {}

  public ShoppingCart(Customer c) {
    customer = c;
  }

  public Long getId() {
    return id;
  }

  public List<CartItem> getItems() {
    return items;
  }

  public void add(CartItem i) {
    items.add(i);
    i.setCart(this);
    updatedAt = LocalDateTime.now();
  }

  public void clear() {
    items.clear();
    updatedAt = LocalDateTime.now();
  }
}
