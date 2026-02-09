package com.company.qic.core;

public final class ScenarioContextHolder {
  private static final ThreadLocal<ScenarioContext> CONTEXT = ThreadLocal.withInitial(ScenarioContext::new);

  private ScenarioContextHolder() {
  }

  public static ScenarioContext get() {
    return CONTEXT.get();
  }

  public static void reset() {
    CONTEXT.remove();
  }
}
