package com.foodordering.service;

import com.foodordering.entity.*;
import com.foodordering.util.*;
import java.util.*;

public class CustomerService {
  public Customer register(String n, String e, String p, String a) {
    n = Validation.required(n, "Name");
    e = Validation.email(e);
    p = Validation.required(p, "Phone");
    a = Validation.required(a, "Address");
    final String nn = n, ee = e, pp = p, aa = a;
    return DB.write(
        em -> {
          if (em.createQuery("select count(c) from Customer c where c.email=:e", Long.class)
                  .setParameter("e", ee)
                  .getSingleResult()
              > 0) throw new IllegalArgumentException("Email already registered.");
          Customer c = new Customer(nn, ee, pp, aa);
          em.persist(c);
          em.flush();
          ShoppingCart cart = new ShoppingCart(c);
          c.setCart(cart);
          em.persist(cart);
          return c;
        });
  }

  public List<Customer> all() {
    return DB.read(
        em ->
            em.createQuery("select c from Customer c order by c.id", Customer.class)
                .getResultList());
  }

  public Customer find(long id) {
    return DB.read(
        em -> {
          Customer c = em.find(Customer.class, id);
          if (c == null) throw new IllegalArgumentException("Customer not found.");
          return c;
        });
  }

  public void delete(long id) {
    DB.write(
        em -> {
          Customer c = em.find(Customer.class, id);
          if (c == null) throw new IllegalArgumentException("Customer not found.");
          if (em.createQuery(
                      "select count(o) from CustomerOrder o where o.customer.id=:i", Long.class)
                  .setParameter("i", id)
                  .getSingleResult()
              > 0)
            throw new IllegalArgumentException("Cannot delete a customer with order history.");
          em.remove(c);
          return null;
        });
  }
}
