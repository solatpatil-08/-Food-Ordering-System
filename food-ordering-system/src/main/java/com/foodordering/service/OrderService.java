package com.foodordering.service;

import com.foodordering.entity.*;
import com.foodordering.util.*;
import java.math.*;
import java.util.*;

public class OrderService {
  public void add(long customerId, long menuId, int quantity) {
    Validation.quantity(quantity);
    DB.write(
        em -> {
          Customer c = em.find(Customer.class, customerId);
          MenuItem m = em.find(MenuItem.class, menuId);
          if (c == null || m == null)
            throw new IllegalArgumentException("Customer or menu item not found.");
          if (!m.isAvailable() || !m.getRestaurant().isActive())
            throw new IllegalArgumentException("Item or restaurant unavailable.");
          ShoppingCart cart = c.getCart();
          if (cart == null) {
            cart = new ShoppingCart(c);
            c.setCart(cart);
            em.persist(cart);
          }
          if (!cart.getItems().isEmpty()
              && !cart.getItems()
                  .get(0)
                  .getMenuItem()
                  .getRestaurant()
                  .getId()
                  .equals(m.getRestaurant().getId()))
            throw new IllegalArgumentException("Cart may contain items from one restaurant only.");
          CartItem old =
              cart.getItems().stream()
                  .filter(i -> i.getMenuItem().getId().equals(menuId))
                  .findFirst()
                  .orElse(null);
          if (old != null) old.setQuantity(old.getQuantity() + quantity);
          else cart.add(new CartItem(m, quantity));
          return null;
        });
  }

  public List<CartItem> cart(long id) {
    return DB.read(
        em ->
            em.createQuery(
                    "select distinct i from CartItem i join fetch i.menuItem m join fetch"
                        + " m.restaurant join fetch m.category where i.cart.customer.id=:id",
                    CartItem.class)
                .setParameter("id", id)
                .getResultList());
  }

  public BigDecimal subtotal(long id) {
    return cart(id).stream()
        .map(i -> i.getMenuItem().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public CustomerOrder checkout(long id) {
    return DB.write(
        em -> {
          Customer c = em.find(Customer.class, id);
          if (c == null || c.getCart() == null || c.getCart().getItems().isEmpty())
            throw new IllegalArgumentException("Customer/cart missing or cart empty.");
          ShoppingCart cart = c.getCart();
          Restaurant r = cart.getItems().get(0).getMenuItem().getRestaurant();
          BigDecimal total = BigDecimal.ZERO;
          for (CartItem i : cart.getItems()) {
            MenuItem m = em.find(MenuItem.class, i.getMenuItem().getId());
            if (m == null || !m.isAvailable())
              throw new IllegalArgumentException("An item is unavailable.");
            total =
                total.add(
                    m.getPrice()
                        .multiply(BigDecimal.valueOf(Validation.quantity(i.getQuantity()))));
          }
          String ref = "FO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
          CustomerOrder o = new CustomerOrder(ref, c, r, total, c.getAddress());
          for (CartItem i : cart.getItems()) {
            MenuItem m = em.find(MenuItem.class, i.getMenuItem().getId());
            BigDecimal line = m.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            o.add(new OrderItem(m, m.getName(), m.getPrice(), i.getQuantity(), line));
          }
          em.persist(o);
          em.flush();
          cart.clear();
          return o;
        });
  }

  public List<CustomerOrder> orders() {
    return DB.read(
        em ->
            em.createQuery(
                    "select distinct o from CustomerOrder o join fetch o.customer join fetch"
                        + " o.restaurant order by o.orderDate desc",
                    CustomerOrder.class)
                .getResultList());
  }

  public void status(long id, OrderStatus next) {
    DB.write(
        em -> {
          CustomerOrder o = em.find(CustomerOrder.class, id);
          if (o == null) throw new IllegalArgumentException("Order not found.");
          if (!allowed(o.getStatus(), next))
            throw new IllegalArgumentException(
                "Invalid transition " + o.getStatus() + " -> " + next);
          o.setStatus(next);
          return null;
        });
  }

  public static boolean allowed(OrderStatus a, OrderStatus b) {
    if (b == OrderStatus.CANCELLED) return a == OrderStatus.PLACED || a == OrderStatus.CONFIRMED;
    return switch (a) {
      case PLACED -> b == OrderStatus.CONFIRMED;
      case CONFIRMED -> b == OrderStatus.PREPARING;
      case PREPARING -> b == OrderStatus.OUT_FOR_DELIVERY;
      case OUT_FOR_DELIVERY -> b == OrderStatus.DELIVERED;
      default -> false;
    };
  }
}
