Feature: Geodatastore web application

  Scenario: Log in
    Given That I am positioned at geodatastore login page
    When I add credentials on login page
    Then I get the overview screen
    Then I logout from the application