package com.foodordering;

import com.foodordering.entity.*;
import com.foodordering.seed.SampleDataLoader;
import com.foodordering.service.*;
import com.foodordering.util.DB;
import java.math.*;
import java.util.*;

public class Main {
  static Scanner s = new Scanner(System.in);
  static CustomerService customers = new CustomerService();
  static CatalogService catalog = new CatalogService();
  static OrderService orders = new OrderService();
  static ReportService reports = new ReportService();

  static String text(String p) {
    System.out.print(p);
    return s.nextLine().trim();
  }

  static int num(String p) {
    while (true)
      try {
        return Integer.parseInt(text(p));
      } catch (Exception e) {
        System.out.println("Enter a valid whole number.");
      }
  }

  static long id(String p) {
    while (true)
      try {
        return Long.parseLong(text(p));
      } catch (Exception e) {
        System.out.println("Enter a valid ID.");
      }
  }

  static BigDecimal money(String p) {
    while (true)
      try {
        return new BigDecimal(text(p));
      } catch (Exception e) {
        System.out.println("Enter a valid amount.");
      }
  }

  static void safe(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      System.out.println("Operation failed: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      System.out.println("FOOD ORDERING MANAGEMENT SYSTEM");
      if (text("Load sample data if database is empty? (y/n): ").equalsIgnoreCase("y"))
        safe(() -> new SampleDataLoader().load());
      boolean run = true;
      while (run) {
        System.out.println(
            "\nMain Menu\n"
                + "1. Customers\n"
                + "2. Restaurants\n"
                + "3. Menu and Categories\n"
                + "4. Cart\n"
                + "5. Checkout\n"
                + "6. Orders\n"
                + "7. Reports\n"
                + "8. Information\n"
                + "0. Exit");
        switch (num("Choice: ")) {
          case 1 -> safe(Main::customerMenu);
          case 2 -> safe(Main::restaurantMenu);
          case 3 -> safe(Main::menuMenu);
          case 4 -> safe(Main::cartMenu);
          case 5 -> safe(Main::checkout);
          case 6 -> safe(Main::orderMenu);
          case 7 -> safe(Main::reportMenu);
          case 8 -> showInformation();
          case 0 -> run = false;
          default -> System.out.println("Invalid choice.");
        }
      }
    } catch (Exception e) {
      System.err.println("Could not start. Check MySQL and DB_* variables: " + e.getMessage());
    } finally {
      DB.close();
    }
  }

  static void customerMenu() {
    System.out.println(
        "\nCustomers\n"
            + "1. Register customer\n"
            + "2. List customers\n"
            + "3. Find customer by ID\n"
            + "4. Delete customer\n"
            + "0. Back");
    switch (num("Choice: ")) {
      case 1 ->
          System.out.println(
              customers.register(
                  text("Name: "), text("Email: "), text("Phone: "), text("Address: ")));
      case 2 -> customers.all().forEach(System.out::println);
      case 3 -> System.out.println(customers.find(id("ID: ")));
      case 4 -> customers.delete(id("ID: "));
    }
  }

  static void restaurantMenu() {
    System.out.println(
        "\nRestaurants\n" + "1. Add restaurant\n" + "2. List restaurants\n" + "0. Back");
    switch (num("Choice: ")) {
      case 1 ->
          System.out.println(
              catalog.addRestaurant(text("Name: "), text("Address: "), text("Phone: ")));
      case 2 -> catalog.restaurants().forEach(System.out::println);
    }
  }

  static void menuMenu() {
    System.out.println(
        "\nMenu and Categories\n"
            + "1. Add category\n"
            + "2. List categories\n"
            + "3. Add food item\n"
            + "4. View restaurant menu\n"
            + "5. Search food\n"
            + "6. Toggle item availability\n"
            + "0. Back");
    switch (num("Choice: ")) {
      case 1 -> System.out.println(catalog.addCategory(text("Name: "), text("Description: ")));
      case 2 -> catalog.categories().forEach(System.out::println);
      case 3 -> {
        catalog.restaurants().forEach(System.out::println);
        catalog.categories().forEach(System.out::println);
        System.out.println(
            catalog.addItem(
                text("Food name: "),
                text("Description: "),
                money("Price: "),
                id("Restaurant ID: "),
                id("Category ID: ")));
      }
      case 4 -> catalog.menu(id("Restaurant ID: ")).forEach(System.out::println);
      case 5 -> catalog.search(text("Search: ")).forEach(System.out::println);
      case 6 ->
          catalog.availability(id("Food ID: "), text("Available? y/n: ").equalsIgnoreCase("y"));
    }
  }

  static void cartMenu() {
    System.out.println(
        "\nCart\n" + "1. Add item\n" + "2. View cart\n" + "3. View subtotal\n" + "0. Back");
    switch (num("Choice: ")) {
      case 1 -> orders.add(id("Customer ID: "), id("Food ID: "), num("Quantity: "));
      case 2 -> orders.cart(id("Customer ID: ")).forEach(System.out::println);
      case 3 -> System.out.println("Subtotal Rs." + orders.subtotal(id("Customer ID: ")));
    }
  }

  static void checkout() {
    long cid = id("Customer ID: ");
    orders.cart(cid).forEach(System.out::println);
    System.out.println("Subtotal Rs." + orders.subtotal(cid));
    if (text("Confirm order? (y/n): ").equalsIgnoreCase("y"))
      System.out.println("Placed: " + orders.checkout(cid));
    else System.out.println("Cancelled checkout.");
  }

  static void orderMenu() {
    System.out.println("\nOrders\n" + "1. List orders\n" + "2. Update order status\n" + "0. Back");
    switch (num("Choice: ")) {
      case 1 -> orders.orders().forEach(System.out::println);
      case 2 -> {
        long id = id("Order ID: ");
        OrderStatus st =
            OrderStatus.valueOf(
                text("Next status (PLACED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED,"
                        + " CANCELLED): ")
                    .toUpperCase());
        orders.status(id, st);
        System.out.println("Updated.");
      }
    }
  }

  static void reportMenu() {
    System.out.println("\nReports");
    System.out.println("Customers: " + reports.count("Customer"));
    System.out.println("Restaurants: " + reports.count("Restaurant"));
    System.out.println("Menu items: " + reports.count("MenuItem"));
    System.out.println("Orders: " + reports.count("CustomerOrder"));
    System.out.println("Delivered sales: Rs." + reports.sales());
    reports.byStatus().forEach(r -> System.out.println(r[0] + ": " + r[1]));
  }

  static void showInformation() {
    System.out.println(
        "\nApplication Information\n"
            + "Java: 25\n"
            + "Persistence: Jakarta Persistence API\n"
            + "ORM: Hibernate 6.6\n"
            + "Database: MySQL 8\n"
            + "Authentication: Not included\n"
            + "Payment processing: Not included");
  }
}
