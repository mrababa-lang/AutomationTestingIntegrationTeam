@qic @regression
Feature: QIC Aggregator API - core flows

  Background:
    Given base URL is configured
    And I use auth "basic"
    And I set header "company" to "002"

  Scenario: Get quote for vehicle policy
    When I send a POST request to "/qicservices/aggregator/GetQuotation" with body from "requests/qic/getQuote.json"
    Then response status should be one of "200,202"

  Scenario: Purchase a policy
    When I send a POST request to "/qicservices/aggregator/PurchasePolicy" with body from "requests/qic/purchasePolicy.json"
    Then response status should be one of "200,202"

  Scenario: Upload policy document
    When I send a POST request to "/qicservices/aggregator/UploadPolicyDocument" with body from "requests/qic/uploadPolicyDocument.json"
    Then response status should be one of "200,202"

  Scenario: Get policy status
    When I send a POST request to "/qicservices/aggregator/GetPolicyStatus" with body from "requests/qic/getPolicyStatus.json"
    Then response status should be one of "200,202"
