package com.foodordering.seed;

import com.foodordering.entity.*;
import com.foodordering.service.*;
import java.math.*;

public class SampleDataLoader {
  public void load() {
    CatalogService c = new CatalogService();
    CustomerService u = new CustomerService();
    if (!c.restaurants().isEmpty() || !u.all().isEmpty()) {
      System.out.println("Database already has data; sample seed skipped.");
      return;
    }
    Category pizza = c.addCategory("Pizza", "Pizza and sides"),
        indian = c.addCategory("Indian", "Indian meals"),
        chinese = c.addCategory("Chinese", "Chinese dishes"),
        dessert = c.addCategory("Desserts", "Sweet dishes"),
        drink = c.addCategory("Beverages", "Drinks");
    Restaurant r1 = c.addRestaurant("Spice Route", "FC Road, Pune", "02040001001"),
        r2 = c.addRestaurant("Slice House", "Baner, Pune", "02040001002"),
        r3 = c.addRestaurant("Wok Express", "Kothrud, Pune", "02040001003");
    c.addItem("Paneer Tikka", "Smoky paneer", new BigDecimal("220"), r1.getId(), indian.getId());
    c.addItem("Veg Thali", "Indian thali", new BigDecimal("180"), r1.getId(), indian.getId());
    c.addItem("Masala Dosa", "Crispy dosa", new BigDecimal("110"), r1.getId(), indian.getId());
    c.addItem("Gulab Jamun", "Sweet dessert", new BigDecimal("70"), r1.getId(), dessert.getId());
    c.addItem("Margherita Pizza", "Cheese pizza", new BigDecimal("249"), r2.getId(), pizza.getId());
    c.addItem("Farmhouse Pizza", "Veg pizza", new BigDecimal("349"), r2.getId(), pizza.getId());
    c.addItem("Garlic Bread", "Garlic side", new BigDecimal("129"), r2.getId(), pizza.getId());
    c.addItem("Hakka Noodles", "Wok noodles", new BigDecimal("160"), r3.getId(), chinese.getId());
    c.addItem(
        "Veg Manchurian", "Saucy veg balls", new BigDecimal("175"), r3.getId(), chinese.getId());
    c.addItem("Fried Rice", "Veg fried rice", new BigDecimal("150"), r3.getId(), chinese.getId());
    c.addItem("Cold Coffee", "Chilled coffee", new BigDecimal("95"), r2.getId(), drink.getId());
    u.register("Aarav Patil", "aarav@example.com", "9876543210", "Pune, Maharashtra");
    u.register("Sneha Joshi", "sneha@example.com", "9876543211", "Baner, Pune");
    u.register("Rohan Shah", "rohan@example.com", "9876543212", "Kothrud, Pune");
    System.out.println("Seed data loaded.");
  }
}
