package com.company.qic.hooks;

import com.company.qic.core.ScenarioContext;
import com.company.qic.core.ScenarioContextHolder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Set;

public class Hooks {
  @Before
  public void beforeScenario(Scenario scenario) {
    ScenarioContext context = ScenarioContextHolder.get();
    Set<String> tags = scenario.getSourceTagNames();
    context.put("tags", tags);
  }

  @After
  public void afterScenario() {
    ScenarioContextHolder.reset();
  }
}
