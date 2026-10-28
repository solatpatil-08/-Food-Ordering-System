package com.foodordering.util;

import jakarta.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DB {
  private static EntityManagerFactory factory;

  static {
    System.setProperty("org.jboss.logging.provider", "jdk");
    Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
  }

  private static EntityManagerFactory createFactory() {
    Map<String, Object> p = new HashMap<>();
    p.put(
        "jakarta.persistence.jdbc.url",
        System.getenv()
            .getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:3306/food_ordering_db?useSSL=false&serverTimezone=UTC"));
    p.put("jakarta.persistence.jdbc.user", System.getenv().getOrDefault("DB_USER", "root"));
    p.put("jakarta.persistence.jdbc.password", System.getenv().getOrDefault("DB_PASSWORD", ""));
    p.put("hibernate.hbm2ddl.auto", System.getenv().getOrDefault("HIBERNATE_DDL", "update"));
    return Persistence.createEntityManagerFactory("foodOrderingPU", p);
  }

  private DB() {}

  private static synchronized EntityManagerFactory factory() {
    if (factory == null) {
      try {
        factory = createFactory();
      } catch (RuntimeException e) {
        throw new IllegalStateException(
            "Could not connect to MySQL. Check DB_URL, DB_USER, and DB_PASSWORD in your .env file.",
            e);
      }
    }
    return factory;
  }

  public static <T> T read(Function<EntityManager, T> f) {
    try (EntityManager e = factory().createEntityManager()) {
      return f.apply(e);
    }
  }

  public static <T> T write(Function<EntityManager, T> f) {
    try (EntityManager e = factory().createEntityManager()) {
      EntityTransaction t = e.getTransaction();
      try {
        t.begin();
        T r = f.apply(e);
        t.commit();
        return r;
      } catch (RuntimeException x) {
        if (t.isActive()) t.rollback();
        throw x;
      }
    }
  }

  public static void close() {
    if (factory != null && factory.isOpen()) factory.close();
  }
}
