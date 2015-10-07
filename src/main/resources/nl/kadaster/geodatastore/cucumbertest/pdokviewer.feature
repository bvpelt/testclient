Feature: Check the pdok viewer

  Scenario: Check bag
    Given I am on the pdok viewer site
    When I search for ,Apeldoorn,Apeldoorn,Gelderland in the geocoder
    And I click on bag layer
    And I click on BAG layer
    Then I get a picture with background and bag objects