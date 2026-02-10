# AutomationTestingIntegrationTeam

## Extent reporting

This project emits an Extent Spark report during the TestNG/Cucumber run and writes it to
`target/extent-report/extent-report.html`.

### Run tests and generate the report

```bash
mvn clean test
```

### Run tests and open the report automatically

```bash
./scripts/run-tests-with-extent.sh
```

If the report is empty, verify that `src/test/resources/extent.properties` is on the test
classpath and that the adapter plugin is registered in
`src/test/java/com/company/framework/runners/TestNgCucumberRunner.java`.
