package com.foodordering.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 80)
  private String name;

  @Column(length = 300)
  private String description;

  @OneToMany(mappedBy = "category")
  private List<MenuItem> menuItems = new ArrayList<>();

  public Category() {}

  public Category(String n, String d) {
    name = n;
    description = d;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return "Category #" + id + " | " + name + " | " + description;
  }
}
