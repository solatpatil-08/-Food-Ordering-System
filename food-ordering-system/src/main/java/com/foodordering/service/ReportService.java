package com.foodordering.service;

import com.foodordering.entity.*;
import com.foodordering.util.DB;
import java.math.*;
import java.util.*;

public class ReportService {
  public long count(String entity) {
    if (!Set.of("Customer", "Restaurant", "MenuItem", "CustomerOrder").contains(entity))
      throw new IllegalArgumentException("Unsupported report.");
    return DB.read(
        em ->
            em.createQuery("select count(x) from " + entity + " x", Long.class).getSingleResult());
  }

  public BigDecimal sales() {
    return DB.read(
        em ->
            em.createQuery(
                    "select coalesce(sum(o.totalAmount),0) from CustomerOrder o where o.status=:s",
                    BigDecimal.class)
                .setParameter("s", OrderStatus.DELIVERED)
                .getSingleResult());
  }

  public List<Object[]> byStatus() {
    return DB.read(
        em ->
            em.createQuery(
                    "select o.status,count(o) from CustomerOrder o group by o.status",
                    Object[].class)
                .getResultList());
  }
}
