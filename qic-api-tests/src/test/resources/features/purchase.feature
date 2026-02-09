@regression @contract @schema @auth:basic @header:company
Feature: QIC PurchasePolicy

  Scenario: Purchase policy using quotation data
    Given I load request body from "data/purchase/happy_path.json"
    And I set json path "$.QuotationNo" to "${ctx:QuotationNo}"
    And I set json path "$.QuoteReferenceNo" to "${ctx:QuoteReferenceNo}"
    When I call "PurchasePolicy"
    Then response status should be 200
    And response should match schema "schemas/purchasePolicy_response.json"
