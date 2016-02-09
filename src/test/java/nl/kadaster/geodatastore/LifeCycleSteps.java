package nl.kadaster.geodatastore;


import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import support.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;

public class LifeCycleSteps {
    private static Logger logger = LoggerFactory.getLogger(LifeCycleSteps.class);
    private final String datasetFileName = "somefile.txt";
    @Autowired
    private TestClient tc;
    @Autowired
    private Configuration conf;
    private CloseableHttpResponse response = null;
    private String datasetId;
    private DatasetQueryResponse datasetQueryResponse;

    @When("^I upload a random file$")
    public void iUploadARandomFile() throws Throwable {
        logger.debug("Start: I upload a random file");

        uploadRandomDataSet();
    }

    @Then("^I get the identifier of the uploaded dataset$")
    public void iGetTheIdentifierOfTheUploadedDataset() throws Throwable {
        logger.debug("Start: I get the identifier of the uploaded dataset");

        checkDataSetId();
    }

    @Given("^The identifier of the uploaded dataset is known$")
    public void theIdentifierOfTheUploadedDatasetIsKnown() throws Throwable {
        logger.debug("Start: The identifier of the uploaded dataset is known");

        createTestClient();

        uploadRandomDataSet();

        checkDataSetId();

        Assert.assertNotNull(datasetId);
        Assert.assertNotEquals(0, datasetId.length());
    }

    @When("^I add descriptive metadata with status draft$")
    public void iAddDescriptiveMetadataWithStatusDraft() throws Throwable {
        logger.debug("Start: I add descriptive metadata with status draft");

        String jsonMetaData;
        String url = conf.getDataset(true) + "/" + datasetId;

        jsonMetaData = createValidMetaData();

        tc.addPostString("metadata", jsonMetaData);

        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);

    }

    @Then("^I get the defined meta data back$")
    public void iGetTheDefinedMetaDataBack() throws Throwable {
        logger.debug("Start: I get the defined meta data back");

        try {
            String resultText = tc.getResultText();
            JsonConverter json = new JsonConverter();

            DatasetServiceResponse dataSet = json.toObject(resultText, DatasetServiceResponse.class);
            Assert.assertNotNull(dataSet);
            Assert.assertEquals(false, dataSet.getError());
            Assert.assertEquals(true, dataSet.getValid());

            logger.debug("Found dataset: {}", dataSet);
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            response.close();
        }

    }

    @Given("^The metadata are uploaded and valid$")
    public void theMetadataAreUploadedAndValid() throws Throwable {
        logger.debug("Start: The metadata are uploaded and valid");

        createTestClient();

        uploadRandomDataSet();

        checkDataSetId();

        Assert.assertNotNull(datasetId);
        Assert.assertNotEquals(0, datasetId.length());

        String jsonMetaData;
        String url = conf.getDataset(true) + "/" + datasetId;

        jsonMetaData = createValidMetaData();

        tc.addPostString("metadata", jsonMetaData);

        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);

    }

    @When("^I publish the uploaded dataset with valid metadata$")
    public void iPublishTheUploadedDatasetWithValidMetadata() throws Throwable {
        logger.debug("Start: I publish the uploaded dataset with valid metadata");

        String url = conf.getDataset(true) + "/" + datasetId;

        boolean publish = true;
        tc.addPostString("publish", Boolean.toString(publish));

        response = tc.sendRequest(url, TestClient.HTTPPOST);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());
    }

    @Then("^I get the defined meta data with status published back$")
    public void iGetTheDefinedMetaDataWithStatusPublishedBack() throws Throwable {
        logger.debug("Start: I get the defined meta data with status published back");

        // The dataset is published
        try {
            JsonConverter jc = new JsonConverter();

            DatasetServiceResponse datasetResponse = jc.toObject(tc.getResultText(), DatasetServiceResponse.class);

            boolean error = datasetResponse.getError();
            String status = datasetResponse.getStatus();

            Assert.assertFalse(error);
            Assert.assertEquals("published", status);

            logger.debug("Datasetresponse: {}", datasetResponse);
        } catch (Exception e) {
            logger.error("Error during conversion of json response: {}", e);
        } finally {
            tc.closeSession();
        }
    }

    @Given("^The dataset is successfully published$")
    public void theDatasetIsSuccessfullyPublished() throws Throwable {
        logger.debug("Start: The dataset is successfully published");

        createTestClient();

        uploadRandomDataSet();

        checkDataSetId();

        Assert.assertNotNull(datasetId);
        Assert.assertNotEquals(0, datasetId.length());

        String jsonMetaData;
        String url = conf.getDataset(true) + "/" + datasetId;

        jsonMetaData = createValidMetaData();

        createTestClient();
        tc.addPostString("metadata", jsonMetaData);

        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);

        boolean publish = true;
        tc.addPostString("publish", Boolean.toString(publish));

        response = tc.sendRequest(url, TestClient.HTTPPOST);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());

        // The dataset is published
        try {
            JsonConverter jc = new JsonConverter();

            DatasetServiceResponse datasetResponse = jc.toObject(tc.getResultText(), DatasetServiceResponse.class);

            boolean error = datasetResponse.getError();
            String status = datasetResponse.getStatus();

            Assert.assertFalse(error);
            Assert.assertEquals("published", status);

            logger.debug("Datasetresponse: {}", datasetResponse);
        } catch (Exception e) {
            logger.error("Error during conversion of json response: {}", e);
        } finally {
            tc.closeSession();
        }

    }

    @When("^I download the published dataset$")
    public void iDownloadThePublishedDataset() throws Throwable {
        logger.debug("Start: I download the published dataset");

        String url = conf.getDownloadUrl(true) + "/" + datasetId;

        response = tc.sendRequest(url, TestClient.HTTPGET);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());

        String resultText = tc.getResultText();
        Assert.assertNotNull(resultText);
        logger.debug("Received file with contents: {}", resultText);
    }

    @Then("^I get the random uploaded file$")
    public void iGetTheRandomUploadedFile() throws Throwable {
        logger.debug("Start: I get the random uploaded file");

        String fname = tc.getContentDispositionFilename();
        Assert.assertEquals(datasetFileName, fname);
    }

    @When("^I delete the dataset$")
    public void iDeleteTheDataset() throws Throwable {
        logger.debug("Start: I delete the dataset");

        String url = conf.getDelete(true) + "/" + datasetId;

        response = tc.sendRequest(url, TestClient.HTTPDELETE);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());

        String resultText = tc.getResultText();
        Assert.assertNotNull(resultText);
        logger.debug("Deleted file with contents: {}", resultText);
    }

    @Given("^There are test datasets with status (.*)$")
    public void thereAreTestDatasetsWithStatusDraft(String status) throws Throwable {
        logger.debug("Start: There are test datasets with status {}", status);

        String title = URLEncoder.encode("lifecycle", "UTF-8");
        String queryParameters = "q=" + title + "&pageSize=100&status=" + status;
        String url = conf.getDatasets(true) + "/" + queryParameters;

        createTestClient();

        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());

        // The dataset is published
        try {
            JsonConverter jc = new JsonConverter();

            datasetQueryResponse = jc.toObject(tc.getResultText(), DatasetQueryResponse.class);

            logger.trace("Datasetresponse: {}", datasetQueryResponse);
        } catch (Exception e) {
            logger.error("Error during conversion of json response: {}", e);
        } finally {
            tc.closeSession();
        }

    }

    @When("^I delete all found datasets$")
    public void iDeleteAllFoundDatasets() throws Throwable {
        logger.debug("Start: I delete all found datasets");

        int len = datasetQueryResponse.getMetadata().length;
        int i = 0;
        for (i = 0; i < len; i++) {
            logger.debug("Deleting element: {} id: {}, status: {}, summary: {}", i, datasetQueryResponse.getMetadata()[i].getIdentifier(),
                    datasetQueryResponse.getMetadata()[i].getStatus(), datasetQueryResponse.getMetadata()[i].getSummary());
            String url = conf.getDelete(true) + "/" + datasetQueryResponse.getMetadata()[i].getIdentifier();
            logger.debug("Deleting dataset with url: {}", url);

            response = tc.sendRequest(url, TestClient.HTTPDELETE);
            Assert.assertNotNull(response);
            Assert.assertEquals(200, tc.getStatusCode());

            String resultText = tc.getResultText();
            Assert.assertNotNull(resultText);
            logger.debug("Deleted file with contents: {}", resultText);

        }
        Assert.assertEquals(len, i);
    }

    @Then("^There are no more test datasets with status (.*)$")
    public void thereAreNoMoreTestDatasetsWithStatusDraft(String status) throws Throwable {
        logger.debug("Start: There are test datasets with status {}", status);

        String title = URLEncoder.encode("lifecycle", "UTF-8");
        String queryParameters = "q=" + title + "&pageSize=100&status=" + status;
        String url = conf.getDatasets(true) + "/" + queryParameters;

        createTestClient();

        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());

        // The dataset is published
        try {
            JsonConverter jc = new JsonConverter();

            datasetQueryResponse = jc.toObject(tc.getResultText(), DatasetQueryResponse.class);

            logger.trace("Datasetresponse: {}", datasetQueryResponse);
        } catch (Exception e) {
            logger.error("Error during conversion of json response: {}", e);
        } finally {
            tc.closeSession();
        }

        Assert.assertEquals(0, datasetQueryResponse.getCount());
    }

    private String createValidMetaData() throws Throwable {
        logger.debug("Create valid metadata");

        String jsonMetaData;
        // I add valid metadata
        String title = "TEST gui lifecycle";
        String summary = "Deze gegevensset is onderdeel van testen en is toegevoegd in het kader van gui lifecycle testen";
        String[] keywords = {"Test", "Gui lifecycle"};
        String lineage = "Bron van de gegevens is onbekend, en niet relevant voor deze test set";
        String resolution = "1000";

        MetaData metaData = new MetaData();
        metaData.setTitle(title);
        metaData.setSummary(summary);
        metaData.setKeywords(keywords);
        String[] topicCategories = getTopicCategories();
        metaData.setTopicCategories(topicCategories);
        LocationKeyValuePair locationKvP = getLocation();
        metaData.setLocationUri(locationKvP.getKey());
        metaData.setLineage(lineage);
        String license = getLicence();
        metaData.setLicense(license);
        metaData.setResolution(resolution);

        JsonConverter jc = new JsonConverter();
        jsonMetaData = jc.toJson(metaData);
        return jsonMetaData;
    }

    private void checkDataSetId() throws Exception {
        logger.debug("Check if datasetid is returned");

        // The datasetId is known
        try {
            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();

                    DatasetServiceResponse datasetResponse = jc.toObject(tc.getResultText(),
                            DatasetServiceResponse.class);

                    datasetId = datasetResponse.getIdentifier();
                    boolean error = datasetResponse.getError();
                    String fileName = datasetResponse.getFileName();

                    Assert.assertNotNull(datasetId);
                    Assert.assertFalse(error);
                    Assert.assertEquals(datasetFileName, fileName);

                    logger.debug("Created dataset: {} filename: {} error: {}", datasetId, fileName, error);
                    logger.debug("Datasetresponse: {}", datasetResponse);
                } catch (Exception e) {
                    logger.error("Error during conversion of json response: {}", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            tc.closeSession();
        }
    }

    private void uploadRandomDataSet() throws Exception {
        logger.debug("Upload a random dataset");
        // And I uploaded a dataset file
        File randomFile = getRandomFile(datasetFileName);
        tc.addPostFile("dataset", randomFile);

        String url = conf.getDataset(true);
        response = tc.sendRequest(url, TestClient.HTTPPOST);

        Assert.assertNotNull(response);
    }

    private String[] getTopicCategories() throws Throwable {
        logger.debug("Get a list of topic catagories from the registry server");

        String[] result = null;
        String url = conf.getRegistry(false) + "/topicCategory";

        createTestClient();
        response = tc.sendRequest(url, TestClient.HTTPGET);

        Assert.assertNotNull(response);

        try {
            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();

                    KeyLabelPairResponse topicResponse = jc.toObject(tc.getResultText(), KeyLabelPairResponse.class);

                    int len = topicResponse.getResponse().length;
                    Assert.assertNotEquals(0, len);
                    if (len > 2) {
                        result = new String[1];
                        result[0] = topicResponse.getResponse()[0].getKey();
                        // result[1] =
                        // topicResponse.getResponse()[1].getLabel();
                    }

                    Assert.assertNotNull(result);

                    logger.debug("Found topics: {}", topicResponse);
                } catch (Exception e) {
                    logger.error("Error during conversion of json response: {}", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            tc.closeSession();
        }

        return result;
    }

    private LocationKeyValuePair getLocation() throws Throwable {
        logger.debug("Get a list of locations from the registry server");

        LocationKeyValuePair result = null;
        String url = conf.getRegistry(false) + "/location?q=Arnhem";

        createTestClient();
        response = tc.sendRequest(url, TestClient.HTTPGET);

        Assert.assertNotNull(response);

        try {
            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();

                    LocationResponse locationResponse = jc.toObject(tc.getResultText(), LocationResponse.class);

                    int len = locationResponse.getResponse().length;
                    Assert.assertNotEquals(0, len);
                    if (len > 0) {
                        result = locationResponse.getResponse()[0];
                    }

                    Assert.assertNotNull(result);

                    logger.debug("LocationResponse - label: {}, key: {}", result.getLabel(), result.getKey());
                } catch (Exception e) {
                    logger.error("Error during conversion of json response: {}", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            tc.closeSession();
        }

        return result;
    }

    private String getLicence() throws Exception {
        logger.debug("Get a list of licenses from the registry server");

        String result = null;
        String url = conf.getRegistry(false) + "/license";

        tc.addHeader("Accept", "application/json");
        response = tc.sendRequest(url, TestClient.HTTPGET);

        Assert.assertNotNull(response);

        try {
            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();

                    KeyLabelPairResponse licenseResponse = jc.toObject(tc.getResultText(), KeyLabelPairResponse.class);

                    int len = licenseResponse.getResponse().length;
                    Assert.assertNotEquals(0, len);
                    if (len > 1) {
                        result = licenseResponse.getResponse()[0].getKey();
                    }

                    Assert.assertNotNull(result);

                    logger.debug("Found licences: {}", licenseResponse);
                } catch (Exception e) {
                    logger.error("Error during conversion of json response: {}", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            tc.closeSession();
        }

        return result;
    }

    private void createTestClient() throws Throwable {
        logger.debug("Create a test client");

        // There is a test client
        tc.addHeader("Accept", "application/json");

        if (conf.isUseproxy()) {
            tc.setProxy(conf.getProxyHost(), conf.getProxyPort());
        }
        Assert.assertNotNull(tc);
    }

    private File getRandomFile(String name) {
        logger.debug("Get a random file with name {}", name);

        File file = new File(name);
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content = "This is dummy text";
            bw.write(content);
            bw.close();

        } catch (Exception e) {
            logger.error("Couldnot create file: {}", name, e);
        }
        return file;
    }

}
