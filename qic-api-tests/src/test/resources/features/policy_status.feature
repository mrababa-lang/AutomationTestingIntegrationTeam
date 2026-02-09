@regression @contract @schema @auth:none @header:none
Feature: QIC GetPolicyStatus

  Scenario: Get policy status
    Given I load request body from "data/policy_status/happy_path.json"
    When I call "GetPolicyStatus"
    Then response status should be 200
    And response should match schema "schemas/getPolicyStatus_response.json"
