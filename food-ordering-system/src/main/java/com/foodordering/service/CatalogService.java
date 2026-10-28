package com.foodordering.service;

import com.foodordering.entity.*;
import com.foodordering.util.*;
import java.math.*;
import java.util.*;

public class CatalogService {
  public Restaurant addRestaurant(String n, String a, String p) {
    return DB.write(
        em -> {
          Restaurant r =
              new Restaurant(
                  Validation.required(n, "Name"),
                  Validation.required(a, "Address"),
                  Validation.required(p, "Phone"));
          em.persist(r);
          return r;
        });
  }

  public List<Restaurant> restaurants() {
    return DB.read(
        em ->
            em.createQuery("select r from Restaurant r order by r.id", Restaurant.class)
                .getResultList());
  }

  public Category addCategory(String n, String d) {
    return DB.write(
        em -> {
          String name = Validation.required(n, "Category");
          if (em.createQuery(
                      "select count(c) from Category c where lower(c.name)=lower(:n)", Long.class)
                  .setParameter("n", name)
                  .getSingleResult()
              > 0) throw new IllegalArgumentException("Category exists.");
          Category c = new Category(name, d);
          em.persist(c);
          return c;
        });
  }

  public List<Category> categories() {
    return DB.read(
        em ->
            em.createQuery("select c from Category c order by c.id", Category.class)
                .getResultList());
  }

  public MenuItem addItem(String n, String d, BigDecimal price, long rid, long cid) {
    String name = Validation.required(n, "Food name");
    BigDecimal p = Validation.price(price);
    return DB.write(
        em -> {
          Restaurant r = em.find(Restaurant.class, rid);
          Category c = em.find(Category.class, cid);
          if (r == null || c == null)
            throw new IllegalArgumentException("Restaurant or category ID not found.");
          if (!r.isActive()) throw new IllegalArgumentException("Restaurant inactive.");
          MenuItem m = new MenuItem(name, d, p, r, c);
          em.persist(m);
          return m;
        });
  }

  public List<MenuItem> menu(long rid) {
    return DB.read(
        em ->
            em.createQuery(
                    "select m from MenuItem m join fetch m.restaurant join fetch m.category where"
                        + " m.restaurant.id=:id",
                    MenuItem.class)
                .setParameter("id", rid)
                .getResultList());
  }

  public List<MenuItem> search(String q) {
    return DB.read(
        em ->
            em.createQuery(
                    "select m from MenuItem m join fetch m.restaurant join fetch m.category where"
                        + " lower(m.name) like :q",
                    MenuItem.class)
                .setParameter("q", "%" + q.toLowerCase() + "%")
                .getResultList());
  }

  public void availability(long id, boolean v) {
    DB.write(
        em -> {
          MenuItem m = em.find(MenuItem.class, id);
          if (m == null) throw new IllegalArgumentException("Menu item not found.");
          m.setAvailable(v);
          return null;
        });
  }
}
