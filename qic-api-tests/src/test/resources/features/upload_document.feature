@regression @contract @schema @auth:basic @header:company
Feature: QIC UploadPolicyDocument

  Scenario: Upload document for policy
    Given I load request body from "data/upload_document/happy_path.json"
    When I call "UploadPolicyDocument"
    Then response status should be 200
    And response should match schema "schemas/uploadPolicyDocument_response.json"
