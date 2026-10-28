package com.foodordering.service;

import static org.junit.jupiter.api.Assertions.*;

import com.foodordering.entity.OrderStatus;
import com.foodordering.util.Validation;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BusinessRulesTest {
  @Test
  void requiredFields() {
    assertThrows(IllegalArgumentException.class, () -> Validation.required(" ", "Name"));
  }

  @Test
  void emailValidation() {
    assertEquals("a@b.com", Validation.email("A@B.COM"));
    assertThrows(IllegalArgumentException.class, () -> Validation.email("bad"));
  }

  @Test
  void priceValidation() {
    assertEquals(new BigDecimal("2.50"), Validation.price(new BigDecimal("2.5")));
    assertThrows(IllegalArgumentException.class, () -> Validation.price(BigDecimal.ZERO));
  }

  @Test
  void quantityValidation() {
    assertEquals(2, Validation.quantity(2));
    assertThrows(IllegalArgumentException.class, () -> Validation.quantity(-1));
  }

  @Test
  void statusTransitions() {
    assertTrue(OrderService.allowed(OrderStatus.PLACED, OrderStatus.CONFIRMED));
    assertTrue(OrderService.allowed(OrderStatus.PLACED, OrderStatus.CANCELLED));
    assertFalse(OrderService.allowed(OrderStatus.PREPARING, OrderStatus.CANCELLED));
    assertFalse(OrderService.allowed(OrderStatus.DELIVERED, OrderStatus.PLACED));
  }
}
