package com.company.qic.core;

import com.company.qic.util.Files;
import com.company.qic.util.FakerUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataResolver {
  private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

  public String loadAndResolve(String path, ScenarioContext context) {
    String body = Files.readResource(path);
    return resolvePlaceholders(body, context);
  }

  public String resolvePlaceholders(String body, ScenarioContext context) {
    if (body == null) {
      return null;
    }
    Matcher matcher = PLACEHOLDER.matcher(body);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
      String token = matcher.group(1);
      String replacement = resolveToken(token, context);
      matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(buffer);
    return buffer.toString();
  }

  private String resolveToken(String token, ScenarioContext context) {
    if (token.startsWith("random.uuid")) {
      return FakerUtil.randomUuid();
    }
    if (token.startsWith("random.alphanum")) {
      int start = token.indexOf('(');
      int end = token.indexOf(')');
      int length = 8;
      if (start > 0 && end > start) {
        length = Integer.parseInt(token.substring(start + 1, end));
      }
      return FakerUtil.randomAlphaNum(length);
    }
    if (token.startsWith("today")) {
      String[] parts = token.split(":");
      String offsetPart = parts[0];
      String format = parts.length > 1 ? parts[1] : "yyyy-MM-dd";
      int offset = 0;
      if (offsetPart.contains("+")) {
        offset = Integer.parseInt(offsetPart.substring(offsetPart.indexOf('+') + 1));
      } else if (offsetPart.contains("-")) {
        offset = -Integer.parseInt(offsetPart.substring(offsetPart.indexOf('-') + 1));
      }
      return LocalDate.now().plusDays(offset).format(DateTimeFormatter.ofPattern(format));
    }
    if (token.startsWith("env:")) {
      String key = token.substring("env:".length());
      String env = System.getenv(key);
      return env == null ? "" : env;
    }
    if (token.startsWith("ctx:")) {
      String key = token.substring("ctx:".length());
      String value = context.getString(key);
      return value == null ? "" : value;
    }
    return "";
  }
}
