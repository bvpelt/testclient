Feature: Geodatastore api

  Scenario: Get list of known codelists
    Given There is a testclient
    When I ask for known codelists
    Then I get a http success status

  Scenario: Upload a random file
    Given There is a testclient
    When I upload a random file
    Then I get a http success status
    And I get the identifier of the uploaded dataset

  Scenario: Add metadata to previous uploaded random file
    Given The identifier of the uploaded dataset is known
    When I add descriptive metadata with status draft
    Then I get a http success status
    And I get the defined meta data back

  Scenario: Publish a previously uploaded random file with valid metadata
   Given The metadata are uploaded and valid
    When I publish the uploaded dataset with valid metadata
    Then I get a http success status
    And I get the defined meta data with status published back

  Scenario: Download a published dataset
    Given The dataset is successfully published
    When I download the published dataset
    Then I get a http success status
    And I get the random uploaded file

  Scenario: Delete a published dataset
    Given The dataset is successfully published
    When I delete the dataset
    Then I get a http success status