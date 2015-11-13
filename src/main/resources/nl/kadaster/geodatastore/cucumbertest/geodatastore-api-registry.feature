Feature: Geodatastore api registry

  Scenario: Get list of known codelists
    Given There is a testclient
    When I ask for known codelists
    Then I get a http success status
