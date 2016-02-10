Feature: Geodatastore api machine 

	This feature test the success lifcycle of a geodatastore dataset.

  The scenario for adding a dataset is:
  - A single step proces
  -- Upload a file, thumbnail, metadata and publish the complete dataset

  After creating a dataset the dataset (published file) can be downloaded

@Machine @Publish 
Scenario: Upload and publish a dataset 
	Given There is a testclient 
	When I upload a random file with thumbnail and metadata 
	Then I get a http success status 
	And I get the identifier of the uploaded dataset 
	
@Machine 
Scenario: Download a published dataset 
	Given The dataset is successfully published 
	When I download the published dataset 
	Then I get a http success status 
	And I get the random uploaded file 
	
@Machine 
Scenario: Delete a published dataset 
	Given The dataset is successfully published 
	When I delete the dataset 
	Then I get a http success status 
