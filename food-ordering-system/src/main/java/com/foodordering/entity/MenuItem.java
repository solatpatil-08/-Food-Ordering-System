package com.foodordering.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "menu_items")
public class MenuItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private boolean available = true;

  @ManyToOne(optional = false)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  public MenuItem() {}

  public MenuItem(String n, String d, BigDecimal p, Restaurant r, Category c) {
    name = n;
    description = d;
    price = p;
    restaurant = r;
    category = c;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String v) {
    description = v;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal v) {
    price = v;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean v) {
    available = v;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public Category getCategory() {
    return category;
  }

  public String toString() {
    return "Food #"
        + id
        + " | "
        + name
        + " | Rs."
        + price
        + " | "
        + category.getName()
        + " | "
        + restaurant.getName()
        + " | available="
        + available;
  }
}
