package com.foodordering.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class Validation {
  private static final Pattern EMAIL =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

  private Validation() {}

  public static String required(String s, String f) {
    if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(f + " is required.");
    return s.trim();
  }

  public static String email(String s) {
    String v = required(s, "Email");
    if (!EMAIL.matcher(v).matches()) throw new IllegalArgumentException("Invalid email address.");
    return v.toLowerCase();
  }

  public static BigDecimal price(BigDecimal p) {
    if (p == null || p.signum() <= 0) throw new IllegalArgumentException("Price must be positive.");
    return p.setScale(2, java.math.RoundingMode.HALF_UP);
  }

  public static int quantity(int q) {
    if (q <= 0) throw new IllegalArgumentException("Quantity must be positive.");
    return q;
  }
}
