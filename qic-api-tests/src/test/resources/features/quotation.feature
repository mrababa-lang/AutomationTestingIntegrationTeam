@regression @contract @schema @auth:basic @header:company
Feature: QIC GetQuotation

  @smoke
  Scenario: Get quotation - happy path
    Given I load request body from "data/quotation/happy_path.json"
    When I call "GetQuotation"
    Then response status should be one of 200,202
    And response should match schema "schemas/getQuotation_response.json"
    And I save response field "$.QuotationNo" as "QuotationNo"
    And I save response field "$.QuoteReferenceNo" as "QuoteReferenceNo"
