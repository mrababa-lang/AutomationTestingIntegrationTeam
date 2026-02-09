package com.company.qic.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class Files {
  private Files() {
  }

  public static String readResource(String path) {
    try (InputStream input = Files.class.getClassLoader().getResourceAsStream(path)) {
      if (input == null) {
        throw new IllegalArgumentException("Resource not found: " + path);
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to read resource: " + path, ex);
    }
  }
}
