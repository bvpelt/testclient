package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import nl.kadaster.geodatastore.JsonConverter;
import nl.kadaster.geodatastore.MetaDataRequest;
import nl.kadaster.geodatastore.MetaDataResponse;
import nl.kadaster.geodatastore.TestClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by bvpelt on 10/3/15.
 */
public class GeoDataStoreApiStepdefs {
    private static Logger logger = LoggerFactory.getLogger(GeoDataStoreApiStepdefs.class);

    private static boolean usePdok = true;
    private boolean useProxy = true;


    private static Configuration conf = new Configuration(usePdok);
    private static String fullurl = conf.getFullUrl();
    private static String baseUrl = fullurl + "/api/v1";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseCodeListUrl = baseUrl + "/registry";

    private CloseableHttpResponse response;
    private TestClient testclient;
    private String lastIdentifier;
    private StringBuffer resultText;
    private MetaDataResponse mdresponse = new MetaDataResponse();
    private JsonConverter json = new JsonConverter();
    private GeoDataStoreApiContext context = GeoDataStoreApiContext.getInstance();
    private UUID uuid = null;


    @Given("^There is a testclient$")
    public void there_is_a_testclient() throws Throwable {
        uuid = UUID.randomUUID();
        logger.info("There is a testclient, run: {}", uuid.toString());
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
        context.setUuid(uuid);
        Assert.assertNotNull(testclient);
    }

    @When("^I ask for known codelists$")
    public void i_ask_for_known_codelists() throws Throwable {
        logger.info("I ask for known codelists");

        // Not yet implemented, topic Codelist is implemented
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        response = testclient.sendRequest(baseCodeListUrl + "/gmd:MD_TopicCategoryCode?cucumberid=" + uuid.toString(), TestClient.HTTPGET);

        Assert.assertNotNull(response);
    }

    @When("^I upload a random file$")
    public void i_upload_a_random_file() throws Throwable {
        logger.info("I upload a random file, run: {}", uuid.toString());

        testclient.setAddRandomFile(true);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        response = testclient.sendRequest(baseDataSetUrl + "?" + "cucumberid=" + uuid.toString(), TestClient.HTTPPOST);

        testclient.setAddRandomFile(false);
        Assert.assertNotNull(response);
    }

    @Then("^I get a http success status$")
    public void i_get_a_http_success_status() throws Throwable {
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Then("^I get the identifier of the uploaded dataset$")
    public void i_get_the_identifier_of_the_uploaded_dataset() throws Throwable {
        logger.info("I get the identifier of the uploaded dataset, run: {}", uuid.toString());
        HttpEntity entity = null;

        try {
            entity = response.getEntity();
            Assert.assertNotNull(entity);
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                Assert.assertNotNull(content);
                resultText = new StringBuffer(content);
                Assert.assertNotNull(resultText);
                Assert.assertNotEquals(0, resultText.toString().length());
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {

                        json.loadString(resultText.toString());
                        lastIdentifier = new String(json.getStringNode("identifier"));
                        context.setDataSetIdentifier(lastIdentifier);
                        context.setResultJson(resultText.toString());
                        logger.info("Identifier: {}", lastIdentifier);
                        // fill metadata with received metadata
                        mdresponse = convertToMetaDataReponse(resultText.toString());
                    }
                }
                logger.info("Result size: {}, content: {}", content.length(), content);
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            response.close();
        }
    }

    /**
     * Retrieve dataset identifier from previous scenario
     *
     * @throws Throwable
     */
    @Given("^The identifier of the uploaded dataset is known$")
    public void the_identifier_of_the_uploaded_dataset_is_known() throws Throwable {
        uuid = context.getUuid();
        logger.info("The identifier of the uploaded dataset is known, run: {}", uuid.toString());
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
        Assert.assertNotNull(testclient);
        lastIdentifier = context.getDataSetIdentifier();
        Assert.assertNotNull(lastIdentifier);
        Assert.assertNotEquals(0, lastIdentifier.length());
    }

    @When("^I add descriptive metadata with status draft$")
    public void i_add_descriptive_metadata_with_status_draft() throws Throwable {
        logger.info("I add descriptive metadata with status draft, run: {}", uuid.toString());
        // Fill metadata record with some values
        MetaDataRequest mdrequest = new MetaDataRequest();
        String jsonString;
        mdrequest.setTitle("TEST METADATA TITLE");
        mdrequest.setSummary("TEST METADATA SUMMARY this is the summary of the test metadata");
        mdrequest.addKeyword("TEST");
        mdrequest.addKeyword("METADATA");
        mdrequest.addTopicCategorie("biota");
        mdrequest.setLocation("Apeldoorn (woonplaats)");
        mdrequest.setLineage("Bron van de gegevens is onbekend");
        mdrequest.setLicense("0"); // == Public Domain
        mdrequest.setResolution(1000);
        JsonConverter jc = new JsonConverter();
        jsonString = jc.getObjectJson(mdrequest);

        testclient.setMetaData(jsonString);
        testclient.setPublish(false);

        logger.info("Send metadata file");

        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier + "?" + "cucumberid=" + uuid.toString();
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);
        testclient.setPublish(false);
    }

    @Then("^I get the defined meta data back$")
    public void i_get_the_defined_meta_data_back() throws Throwable {
        logger.info("I get the defined meta data back, run: {}", uuid.toString());
        HttpEntity entity = null;

        try {
            entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {


                        json.loadString(resultText.toString());
                        lastIdentifier = json.getStringNode("identifier");
                        context.setResultJson(resultText.toString());
                        logger.info("Identifier: {}", lastIdentifier);
                        // fill metadata with received metadata
                        mdresponse = convertToMetaDataReponse(resultText.toString());
                    }
                }
                logger.info("Result size: {}, content: {}", content.length(), content);
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            response.close();
        }
    }

    @Given("^The metadata are uploaded and valid$")
    public void the_metadata_are_uploaded_and_valid() throws Throwable {
        uuid = context.getUuid();
        logger.info("The metadata are uploaded and valid, run: {}", uuid.toString());
        json.loadString(context.getResultJson());
        lastIdentifier = context.getDataSetIdentifier();
        logger.info("Identifier: {}", lastIdentifier);
        // fill metadata with received metadata
        mdresponse = convertToMetaDataReponse(context.getResultJson());
        if (mdresponse == null) {
            throw new Exception("No medatadata defined");
        }
    }

    @When("^I publish the uploaded dataset with valid metadata$")
    public void i_publish_the_uploaded_dataset_with_valid_metadata() throws Throwable {
        logger.info("I publish the uploaded dataset with valid metadata, run: {}", uuid.toString());
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
        Assert.assertNotNull(testclient);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        testclient.setPublish(true);
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier + "?" + "cucumberid=" + uuid.toString();
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);
        testclient.setPublish(false);
    }

    @Then("^I get the defined meta data with status published back$")
    public void i_get_the_defined_meta_data_with_status_published_back() throws Throwable {
        logger.info("I get the defined meta data with status published back, run: {}", uuid.toString());
        HttpEntity entity = null;

        try {

            entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {

                        json.loadString(resultText.toString());
                        lastIdentifier = json.getStringNode("identifier");
                        logger.info("Identifier: {}", lastIdentifier);
                        String status = json.getStringNode("status");

                        if ((status == null) && (status.length() == 0) && (!status.equals("published"))) {
                            throw new Error("Status not equal published");
                        }

                    }
                }
                logger.info("Result size: {}, content: {}", content.length(), content);
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            response.close();
        }
    }

    @Given("^The dataset is successfully published$")
    public void the_dataset_is_successfully_published() throws Throwable {
        uuid = context.getUuid();
        logger.info("The dataset is successfully published, run: {}", uuid.toString());
        json.loadString(context.getResultJson());
        lastIdentifier = context.getDataSetIdentifier();
        logger.info("Identifier: {}", lastIdentifier);
        // fill metadata with received metadata
        mdresponse = convertToMetaDataReponse(context.getResultJson());
        Assert.assertNotNull(mdresponse);
        Assert.assertEquals("published", mdresponse.getStatus());
    }

    @When("^I download the published dataset$")
    public void i_download_the_published_dataset() throws Throwable {
        logger.info("I download the published dataset, run: {}", uuid.toString());
        String download_url = mdresponse.getUrl();
        logger.info("Download from dataset {} file {}", mdresponse.getIdentifier(), download_url);
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
        Assert.assertNotNull(testclient);
        testclient.addHeader("Accept", "*/*;");
        response = testclient.sendRequest(download_url, TestClient.HTTPGET);
    }

    @Then("^I get the random uploaded file$")
    public void i_get_the_random_uploaded_file() throws Throwable {
        logger.info("I get the random uploaded file, run: {}", uuid.toString());
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @When("^I delete the dataset$")
    public void i_delete_the_dataset() throws Throwable {
        uuid = context.getUuid();
        logger.info("I delete the dataset, run: {}", uuid.toString());
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
        Assert.assertNotNull(testclient);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier;
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPDELETE);
    }


    private MetaDataResponse convertToMetaDataReponse(final String jsonString) {
        json.loadString(jsonString);
        MetaDataResponse mdr = new MetaDataResponse();
        // fill metadata with received metadata

        mdr.setTitle(json.getStringNode("title"));
        mdr.setSummary(json.getStringNode("summary"));
        mdr.setKeywords(json.getStringArray("keywords"));
        mdr.setTopicCategories(json.getStringArray("topicCategories"));
        mdr.setLocation(json.getStringNode("location"));
        mdr.setLineage(json.getStringNode("lineage"));
        mdr.setLicense(json.getStringNode("license"));
        String res = json.getStringNode("resolution");
        if ((null == res) || (res.length() == 0) || (res.equals("null"))) {
            res = "0";
        }
        mdr.setResolution(Integer.parseInt(res));
        mdr.setIdentifier(json.getStringNode("identifier"));
        mdr.setUrl(json.getStringNode("url"));
        mdr.setExtent(json.getStringNode("extent"));
        mdr.setError(json.getStringNode("error"));
        mdr.setMessages(json.getStringArray("messages"));
        mdr.setStatus(json.getStringNode("status"));
        mdr.setFiletype(json.getStringNode("fileType"));
        mdr.setLocationUri(json.getStringNode("locationUri"));
//        mdr.setThumbnail(json.getStringNode("thumbnail"));
        mdr.setChangeDate(json.getStringNode("changeDate"));
        mdr.setValid(json.getStringNode("valid"));

        return mdr;
    }
}