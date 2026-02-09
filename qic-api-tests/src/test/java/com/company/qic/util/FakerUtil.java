package com.company.qic.util;

import java.security.SecureRandom;
import java.util.UUID;

public final class FakerUtil {
  private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom RANDOM = new SecureRandom();

  private FakerUtil() {
  }

  public static String randomUuid() {
    return UUID.randomUUID().toString();
  }

  public static String randomAlphaNum(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
    }
    return builder.toString();
  }
}
