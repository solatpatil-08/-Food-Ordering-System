package com.foodordering.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_cart_menu_item",
            columnNames = {"cart_id", "menu_item_id"}))
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "cart_id", nullable = false)
  private ShoppingCart cart;

  @ManyToOne(optional = false)
  @JoinColumn(name = "menu_item_id", nullable = false)
  private MenuItem menuItem;

  @Column(nullable = false)
  private int quantity;

  public CartItem() {}

  public CartItem(MenuItem m, int q) {
    menuItem = m;
    quantity = q;
  }

  public ShoppingCart getCart() {
    return cart;
  }

  public void setCart(ShoppingCart v) {
    cart = v;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int v) {
    quantity = v;
  }
}
