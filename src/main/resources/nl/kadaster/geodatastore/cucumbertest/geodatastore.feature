Feature: Geodatastore

  Scenario: Upload a random file
    Given There is a testclient
    When I upload a random file
    Then I get the identifier of the uploaded dataset
