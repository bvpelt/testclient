Feature: Geodatastore api success lifecycle

This feature test the lifcycle of a geodatastore dataset.
Possible scenarios for adding a dataset are:
- A multi step proces
-- Upload a file (this creates a dataset)
-- Add meta data to the created dataset
-- Publish the dataset
- A single step proces
-- Upload a file, thumbnail, metadata and publish the complete dataset

After creating a dataset the dataset (published file) can be downloaded

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
