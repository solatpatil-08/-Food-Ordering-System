package com.foodordering.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private CustomerOrder customerOrder;

  @ManyToOne
  @JoinColumn(name = "menu_item_id")
  private MenuItem menuItem;

  @Column(nullable = false, length = 120)
  private String itemName;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal lineTotal;

  public OrderItem() {}

  public OrderItem(MenuItem m, String n, BigDecimal p, int q, BigDecimal t) {
    menuItem = m;
    itemName = n;
    unitPrice = p;
    quantity = q;
    lineTotal = t;
  }

  public void setCustomerOrder(CustomerOrder v) {
    customerOrder = v;
  }

  public String toString() {
    return itemName + " | Rs." + unitPrice + " x " + quantity + " = Rs." + lineTotal;
  }
}
