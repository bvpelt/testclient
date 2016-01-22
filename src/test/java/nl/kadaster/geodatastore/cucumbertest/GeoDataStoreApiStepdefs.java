package nl.kadaster.geodatastore.cucumbertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.kadaster.geodatastore.*;

import org.junit.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
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
    private static String fullnaurl = conf.getFullNaUrl();
    private static String baseUrl = fullurl + "/api/v1";
    private static String baseNaUrl = fullnaurl + "/api/v1";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseRegistriesUrl = baseNaUrl + "/registries";
    private static String baseRegistryUrl = baseNaUrl + "/registry";

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
            //testclient.setProxy("ssl-proxy.cs.kadaster.nl", 8080);
        }
        context.setUuid(uuid);
        Assert.assertNotNull(testclient);
    }

    @When("^I ask for known registrie services$")
    public void i_ask_for_known_codelists() throws Throwable {
        logger.info("Get list of known registrie services");

        // Not yet implemented, topic Codelist is implemented
        testclient.addHeader("Accept", "application/json");
        response = testclient.sendRequest(baseRegistriesUrl, TestClient.HTTPGET);

        Assert.assertNotNull(response);
    }

    @When("^I ask register service (.*) with parameter (.*)$")
    public void i_ask_register_service_license_with_parameter(String regServiceName, String parameters) throws Throwable {
        logger.info("I ask register service {} with parameter {}", regServiceName, parameters);

        // Get al knownn names with url to the service url
        testclient.addHeader("Accept", "application/json");
        response = testclient.sendRequest(baseRegistriesUrl, TestClient.HTTPGET);

        HttpEntity entity = null;
        String testUri = null;
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

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            RegistryServicesResponse myObjects = mapper.readValue(resultText.toString(), RegistryServicesResponse.class);

                            logger.debug("Found objects: {}", myObjects.getRegistries().toString());
                            boolean found = false;
                            int i = 0;
                            int maxLen = myObjects.getRegistries().length;
                            while ((!found) && (i < maxLen)) {
                                RegistryService rs = myObjects.getRegistries()[i];
                                if (regServiceName.equals(rs.getName())) {
                                    testUri = rs.getUrl();
                                    found = true;
                                }
                                i++;
                            }
                        } catch (IOException e) {
                            logger.error("Error during conversion of json response: {}", e);
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

        if (testUri != null) {
            // Not yet implemented, topic Codelist is implemented
            testclient.addHeader("Accept", "application/json");
            testUri = testUri + "?" + parameters;
            logger.info("Request service: {} with parameters: {} on uri: {}", regServiceName, parameters, testUri);
            response = testclient.sendRequest(testUri, TestClient.HTTPGET);
        } else {
            logger.error("No service url found for registry service {}", regServiceName);
        }
        Assert.assertNotNull(response);
    }

    @When("^I ask register service (.*) without filtering$")
    public void i_ask_register_serivce_without_filtering(String regServiceName) throws Throwable {
        logger.info("I ask register service {} without filtering", regServiceName);

        // Get al knownn names with url to the service url
        testclient.addHeader("Accept", "application/json");
        response = testclient.sendRequest(baseRegistriesUrl, TestClient.HTTPGET);

        HttpEntity entity = null;
        String testUri = null;
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

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            RegistryServicesResponse myObjects = mapper.readValue(resultText.toString(), RegistryServicesResponse.class);

                            logger.debug("Found objects: {}", myObjects.getRegistries().toString());
                            boolean found = false;
                            int i = 0;
                            int maxLen = myObjects.getRegistries().length;
                            while ((!found) && (i < maxLen)) {
                                RegistryService rs = myObjects.getRegistries()[i];
                                if (regServiceName.equals(rs.getName())) {
                                    testUri = rs.getUrl();
                                    found = true;
                                }
                                logger.debug("Searching for registry service {} current value {} result {}", regServiceName, rs.getName(), found);
                                i++;
                            }
                        } catch (IOException e) {
                            logger.error("Error during conversion of json response: {}", e);
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

        if (testUri != null) {
            // Not yet implemented, topic Codelist is implemented
            testclient.addHeader("Accept", "application/json");
            response = testclient.sendRequest(testUri, TestClient.HTTPGET);
        } else {
            logger.error("No service url found for registry service {}", regServiceName);
        }
        Assert.assertNotNull(response);
    }


    private File getRandomFile() {
        String testFile = "somefile.txt";
        File file = new File(testFile);
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
            logger.error("Couldnot create file: {}", testFile, e);
        }
        return file;
    }

    @When("^I upload a random file$")
    public void i_upload_a_random_file() throws Throwable {
        logger.info("I upload a random file, run: {}", uuid.toString());

        File randomFile = getRandomFile();
        testclient.addPostFile("dataset", randomFile);


        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        response = testclient.sendRequest(baseDataSetUrl + "?" + "cucumberid=" + uuid.toString(), TestClient.HTTPPOST);

        Assert.assertNotNull(response);
    }

    @Then("^I get a http success status$")
    public void i_get_a_http_success_status() throws Throwable {
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Then("^I get http status (\\d+)$")
    public void i_get_http_status(int status) throws Throwable {
        Assert.assertNotNull(response);
        Assert.assertEquals(status, response.getStatusLine().getStatusCode());
    }

    private boolean nameInServiceList(String name, RegistryServicesResponse myObjects) {
        boolean found = false;
        RegistryService[] services = myObjects.getRegistries();
        int len = services.length;
        int i = 0;
        while ((!found) && (i < len)) {
            found = name.equals(services[i].getName());
            i++;
        }
        return found;
    }

    @Then("^I get a list of (\\d+) known registrie services with names \\'(.*)\\'$")
    public void i_get_a_list_of_known_registrie_services(int numServices, String names) throws Throwable {
        logger.info("I get a list of {} known registrie services with names {}, run: {}", numServices, names, uuid.toString());
        String [] registryNames = names.split(", ");

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

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            RegistryServicesResponse myObjects = mapper.readValue(resultText.toString(), RegistryServicesResponse.class);

                            for (String regName: registryNames) {
                                logger.debug("Checking if {} exists", regName);
                                boolean found = nameInServiceList(regName, myObjects);
                                Assert.assertEquals("service: " + regName + " found", true, found);
                            }
                            Assert.assertEquals(numServices, myObjects.getRegistries().length);
                            logger.debug("Found objects: {}", myObjects.getRegistries().toString());
                        } catch (IOException e) {
                            logger.error("Error during conversion of json response: {}", e);
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
        // mdrequest.setLocation("Stampersgat (woonplaats)");
        mdrequest.setLocationUri("http://geodatastore.pdok.nl/registry/location#Stampersgat_residence");
        mdrequest.setLineage("Bron van de gegevens is onbekend");
        mdrequest.setLicense("http://creativecommons.org/licenses/by/3.0/nl/"); // == Public Domain
        mdrequest.setResolution(1000);
        JsonConverter jc = new JsonConverter();
        jsonString = jc.getObjectJson(mdrequest);

        boolean publish = false;
        testclient.addPostString("metadata", jsonString, ContentType.APPLICATION_JSON);
        testclient.addPostString("publish", Boolean.toString(publish));

        logger.info("Send metadata file");

        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier + "?" + "cucumberid=" + uuid.toString();
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);

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

        boolean publish = true;
        testclient.addPostString("publish", Boolean.toString(publish));

        String datasetUrl = baseDataSetUrl + "/" + lastIdentifier + "?" + "cucumberid=" + uuid.toString();
        response = testclient.sendRequest(datasetUrl, TestClient.HTTPPOST);

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
                        context.setResultJson(resultText.toString());
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

    private String checkedUrl(final String downloadUrl) {
        String result;

        URI uri = URI.create(downloadUrl);

        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String authority = uri.getAuthority();
        String query = uri.getQuery();
        String fragment = uri.getFragment();

        String[] hostparts = host.split(":");

        result = scheme + "://" + hostparts[0] + path;

        return result;
    }

    @When("^I download the published dataset$")
    public void i_download_the_published_dataset() throws Throwable {
        logger.info("I download the published dataset, run: {}", uuid.toString());
        String download_url = mdresponse.getUrl();

        // if the url contains a portnumber, remove it. otherwise ssl can't check web site
        // geodatastore returns sometheing like
        //   https://test.geodatastore.pdok.nl:443/id/dataset/f69180a3-ebac-4af8-a73d-8b7897d6cded
        // this should be converted
        //   https://test.geodatastore.pdok.nl/id/dataset/f69180a3-ebac-4af8-a73d-8b7897d6cded
        String url = checkedUrl(download_url);
        logger.info("Cleaned url, download_url: {}, url: {}", download_url, url);
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


    private File getThumbnailFile() {
        String thumbnailFileName = "xls.png";
        File file = null;

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            file = new File(classLoader.getResource(thumbnailFileName).getFile());
        } catch (Exception e) {
            logger.error("Couldnot find file: {}", thumbnailFileName, e);
        }
        return file;
    }

    @When("^I upload a random file with thumbnail and metadata$")
    public void i_upload_a_random_file_with_thumbnail_and_metadata() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        logger.info("I upload a random file with thumbnail and metadata: {}", uuid.toString());

        File randomFile = getRandomFile();
        testclient.addPostFile("dataset", randomFile);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");


        // Fill metadata record with some values
        MetaDataRequest mdrequest = new MetaDataRequest();
        String jsonString;
        mdrequest.setTitle("TEST METADATA MACHINE ONECALL");
        mdrequest.setSummary("TEST METADATA SUMMARY of the machine one call this is the summary of the test metadata");
        mdrequest.addKeyword("TEST");
        mdrequest.addKeyword("METADATA");
        mdrequest.addTopicCategorie("biota");
        // mdrequest.setLocation("Stampersgat (woonplaats)");
        mdrequest.setLocationUri("http://geodatastore.pdok.nl/registry/location#Stampersgat_residence");
        mdrequest.setLineage("Bron van de gegevens is onbekend");
        mdrequest.setLicense("http://creativecommons.org/licenses/by/3.0/nl/"); // == Public Domain
        mdrequest.setResolution(1000);
        JsonConverter jc = new JsonConverter();
        jsonString = jc.getObjectJson(mdrequest);


        boolean publish = true;
        testclient.addPostString("metadata", jsonString, ContentType.APPLICATION_JSON);
        testclient.addPostString("publish", Boolean.toString(publish));

        File thumbnailFile = getThumbnailFile();
        testclient.addPostFile("thumbmail", thumbnailFile);


        response = testclient.sendRequest(baseDataSetUrl + "?" + "cucumberid=" + uuid.toString(), TestClient.HTTPPOST);

        Assert.assertNotNull(response);
    }
}