Feature: Geodatastore api success lifecycle


  This feature test the success lifcycle of a geodatastore dataset.

  The scenario for adding a dataset is:
  - A multi step proces
  -- Upload a file (this creates a dataset)
  -- Add meta data to the created dataset
  -- Publish the dataset

  After creating a dataset the dataset (published file) can be downloaded

  There is a dependency in the order the scenario's are executed.
  Normally there is no context between scenarios. The necessary part of
  the context should be saved at exit of a scenario and restored at start
  of the next scenario.

  @Lifecycle @Upload
  Scenario: Upload a random file

  A random file is uploaded which as a side effect creates a
  dataset. As a result the identified of the created dataset is
  returned.

    Given There is a testclient
    When I upload a random file
    Then I get a http success status
    And I get the identifier of the uploaded dataset

  @Lifecycle @AddMetaData
  Scenario: Add metadata to previous uploaded random file

  Meta data is added to a previously created dataset. The meta data provided
  is valid and should pass the metadata validation (this is the success lifecycle!)

    Given The identifier of the uploaded dataset is known
    When I add descriptive metadata with status draft
    Then I get a http success status
    And I get the defined meta data back

  @Lifecycle @Publish
  Scenario: Publish a previously uploaded random file with valid metadata
    Given The metadata are uploaded and valid
    When I publish the uploaded dataset with valid metadata
    Then I get a http success status
    And I get the defined meta data with status published back

  @Lifecycle @Download
  Scenario: Download a published dataset
    Given The dataset is successfully published
    When I download the published dataset
    Then I get a http success status
    And I get the random uploaded file

  @Lifecycle @Delete
  Scenario: Delete a published dataset
    Given The dataset is successfully published
    When I delete the dataset
    Then I get a http success status

  @Lifecycle @Cleanup
  Scenario Outline: Retrieve datasets
    Given There are test datasets with status <uploadStatus>
    When I delete all found datasets
    Then There are no more test datasets with status <uploadStatus>

    Examples:
      | uploadStatus |
      | draft        |
      | published    |
