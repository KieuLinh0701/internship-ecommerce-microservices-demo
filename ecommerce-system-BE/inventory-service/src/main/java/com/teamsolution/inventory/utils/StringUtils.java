package com.teamsolution.inventory.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils {

  private static final Pattern SPECIAL_CHARS = Pattern.compile("[^a-zA-Z0-9\\s]");
  private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

  private static String normalize(String input) {
    if (input == null || input.isBlank()) return "";

    String cleaned = input.replace("+", " ").replace(",", " ").replace("|", " ");

    cleaned = SPECIAL_CHARS.matcher(cleaned).replaceAll("");

    String normalized = Normalizer.normalize(cleaned, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

    normalized = MULTI_SPACE.matcher(normalized).replaceAll(" ").trim();

    return normalized;
  }

  public static String toSlug(String input) {
    String base = normalize(input);

    return base.toLowerCase(Locale.ENGLISH).replace(" ", "-").replaceAll("-{2,}", "-");
  }

  public static String toSku(String input) {
    String base = normalize(input).toUpperCase(Locale.ENGLISH).replace(" ", "");

    String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    return base + "-" + uuid;
  }
}
