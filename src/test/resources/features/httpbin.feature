@smoke @regression
Feature: HttpBin - Basic responses

  Scenario: Get - returns query args
    Given base URL is configured
    When I send a GET request to "/get?foo=bar"
    Then response status should be 200
    And json path "args.foo" should equal "bar"
    And response matches schema "schemas/httpbin/get_200.schema.json"

  @negative
  Scenario: Response headers - returns error response with JSON body
    Given base URL is configured
    When I send a GET request to "/response-headers?status=404"
    Then response status should be 404
    And response matches schema "schemas/httpbin/responseHeaders_404.schema.json"
