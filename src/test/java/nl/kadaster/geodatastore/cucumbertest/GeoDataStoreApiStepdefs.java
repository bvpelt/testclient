package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.kadaster.geodatastore.JsonConverter;
import nl.kadaster.geodatastore.MetaData;
import nl.kadaster.geodatastore.MetaDataResponse;
import nl.kadaster.geodatastore.TestClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by bvpelt on 10/3/15.
 */
public class GeoDataStoreApiStepdefs {
    private static Logger logger = LoggerFactory.getLogger(GeoDataStoreApiStepdefs.class);

    /*
    //private static String host = "http://test1:password@ngr3.geocat.net";
    private static String scheme = "http";
    private static String username = "test1";
    private static String password = "password";
    private static String host = "ngr3.geocat.net";
    /*
    private static String scheme = "https";
    private static String username = "WPM";
    private static String password = "testtest";
    private static String host = "test.geodatastore.pdok.nl";
    */
    private static boolean usePdok = true;
    private static Configuration conf = new Configuration(usePdok);
    private static String fullurl = conf.getFullUrl();
    private static String baseUrl = fullurl + "/geonetwork/api/v1";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseCodeListUrl = baseUrl + "/registry";
    private CloseableHttpResponse response;

    private TestClient testclient = null;
    private String lastIdentifier = null;
    private StringBuffer resultText = null;
    private MetaDataResponse mdresponse = null;
    private boolean useProxy = false;

    @Given("^There is a testclient$")
    public void there_is_a_testclient() throws Throwable {
        logger.info("Create testclient");
        testclient = new TestClient();
        if (useProxy) {
            testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
    }

    @When("^I upload a random file$")
    public void i_upload_a_random_file() throws Throwable {
        logger.info("Upload random file");
        testclient.setAddRandomFile(true);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        response = testclient.sendRequest(baseDataSetUrl, TestClient.HTTPPOST);
        testclient.setAddRandomFile(false);
    }

    @Then("^I get a http success status$")
    public void i_get_a_http_success_status() throws Throwable {
        logger.info("Read return code");
        if (null == response) {
            throw new Exception("No response object available");
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Invalid status code");
        }
    }

    @Then("^I get the identifier of the uploaded dataset$")
    public void i_get_the_identifier_of_the_uploaded_dataset() throws Throwable {
        logger.info("Extract last identifier");
        HttpEntity entity = null;

        try {
            entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        mdresponse = new MetaDataResponse();
                        JsonConverter json = new JsonConverter();

                        json.loadString(resultText.toString());
                        lastIdentifier = json.getStringNode("identifier");
                        logger.info("Identifier: {}", lastIdentifier);
                        // fill metadata with received metadata

                        mdresponse.setTitle(json.getStringNode("title"));
                        mdresponse.setSummary(json.getStringNode("summary"));
                        //mdresponse.setKeywords(json.getStringNode("keywords"));
                        mdresponse.setLocation(json.getStringNode("location"));
                        //  md.setLineage(json.getStringNode("lineage"));
                        mdresponse.setLicense(json.getStringNode("license"));
                        mdresponse.setResolution(Integer.parseInt(json.getStringNode("resolution")));
                        mdresponse.setIdentifier(json.getStringNode("identifier"));
                        mdresponse.setUrl(json.getStringNode("url"));
                        mdresponse.setExtent(json.getStringNode("extent"));
                        mdresponse.setError(json.getStringNode("error"));
                        mdresponse.setMessages(json.getStringNode("messages"));
                        mdresponse.setStatus(json.getStringNode("status"));
                        mdresponse.setFiletype(json.getStringNode("fileType"));
                        mdresponse.setChangeDate(json.getStringNode("changeDate"));
                        mdresponse.setValid(json.getStringNode("valid"));
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

    @Given("^The identifier of the uploaded dataset is known$")
    public void the_identifier_of_the_uploaded_dataset_is_known() throws Throwable {
        if ((null == lastIdentifier) || (lastIdentifier.length() == 0)) {
            throw new Exception("No identifier found");
        }
    }

    @When("^I add descriptive metadata with status draft$")
    public void i_add_descriptive_metadata_with_status_draft() throws Throwable {
        // Fill metadata record with some values
        MetaData metadata = new MetaData();
        metadata.setTitle("Feature: Geodatastore");
        metadata.setSummary("This is a dataset uploaded for test purposes");
        metadata.setKeywords("test, upload");
        metadata.setTopicCategories("NGR");
        metadata.setLocation("Apeldoorn");
        //md.setLineage("vervallen");
        metadata.setResolution(2000);

        // write metadata to file
        String testFile = "somefile.txt";
        File file = new File(testFile);
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            JsonConverter jcon = new JsonConverter();

            bw.write(jcon.getObjectJson(metadata));
            bw.close();

        } catch (Exception e) {
            logger.error("Couldnot create file: {}", testFile, e);
            throw new Exception("Couldnot create file", e);
        }

        logger.info("Upload metadata file");
        testclient.getFileEntity(testFile);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier;
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);
        testclient.setAddFile(false);
    }

    @Then("^I get the defined meta data back$")
    public void i_get_the_defined_meta_data_back() throws Throwable {
        logger.info("Extract last identifier");
        HttpEntity entity = null;

        try {
            md = null;
            entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        md = new MetaData();
                        JsonConverter json = new JsonConverter();

                        json.loadString(resultText.toString());
                        lastIdentifier = json.getStringNode("identifier");
                        logger.info("Identifier: {}", lastIdentifier);
                        // fill metadata with received metadata

                        md.setTitle(json.getStringNode("title"));
                        md.setSummary(json.getStringNode("summary"));
                        md.setKeywords(json.getStringNode("keywords"));
                        md.setLocation(json.getStringNode("location"));
                        //  md.setLineage(json.getStringNode("lineage"));
                        md.setLicense(json.getStringNode("license"));
                        md.setResolution(Integer.parseInt(json.getStringNode("resolution")));
                        md.setIdentifier(json.getStringNode("identifier"));
                        md.setUrl(json.getStringNode("url"));
                        md.setExtent(json.getStringNode("extent"));
                        md.setError(json.getStringNode("error"));
                        md.setMessages(json.getStringNode("messages"));
                        md.setStatus(json.getStringNode("status"));
                        md.setFiletype(json.getStringNode("fileType"));
                        md.setChangeDate(json.getStringNode("changeDate"));
                        md.setValid(json.getStringNode("valid"));

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
        if (md == null) {
            throw new Exception("No medatadata defined");
        }
    }

    @When("^I publish the uploaded dataset with valid metadata$")
    public void i_publish_the_uploaded_dataset_with_valid_metadata() throws Throwable {

        logger.info("Upload metadata file");

        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        testclient.setPublish(true);
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier;
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);
        testclient.setPublish(false);
    }

    @Then("^I get the defined meta data with status published back$")
    public void i_get_the_defined_meta_data_with_status_published_back() throws Throwable {
        logger.info("Check status");
        HttpEntity entity = null;

        try {
            md = null;
            entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        md = new MetaData();
                        JsonConverter json = new JsonConverter();

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
        if (md.getStatus().equals("published")) {
            throw new Exception("No published dataset");
        }
    }

    @When("^I download the published dataset$")
    public void i_download_the_published_dataset() throws Throwable {
        String download_url = md.getUrl();
        testclient.addHeader("Accept", "*/*;");
        response = testclient.sendRequest(download_url, TestClient.HTTPGET);
    }

    @Then("^I get the random uploaded file$")
    public void i_get_the_random_uploaded_file() throws Throwable {
        if (null == response) {
            throw new Exception("No response object available");
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Error in upload random file");
        }
    }

    @When("^I delete the dataset$")
    public void i_delete_the_dataset() throws Throwable {

        logger.info("Delete dataset");

        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier;
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPDELETE);
    }


}
