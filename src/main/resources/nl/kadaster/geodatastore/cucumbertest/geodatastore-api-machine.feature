Feature: Geodatastore api machine

  Scenario: Upload and publish a dataset
    Given There is a testclient
    When I upload a random file with thumbnail and metadata
    Then I get a http success status
    And I get the identifier of the uploaded dataset

  Scenario: Download a published dataset
    Given The dataset is successfully published
    When I download the published dataset
    Then I get a http success status
    And I get the random uploaded file

  Scenario: Delete a published dataset
    Given The dataset is successfully published
    When I delete the dataset
    Then I get a http success status
