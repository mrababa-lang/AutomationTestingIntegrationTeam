# AutomationTestingIntegrationTeam

## Allure reporting

This project emits Allure results during the TestNG/Cucumber run and generates the report from
`target/allure-results`.

### Run tests and generate the report

```bash
mvn clean test
mvn allure:report
```

To serve the report locally:

```bash
mvn allure:serve
```

If the report is empty, verify that `target/allure-results` contains JSON files and that the
TestNG listener `io.qameta.allure.testng.AllureTestNg` is registered in
`src/test/resources/testng.xml`.
