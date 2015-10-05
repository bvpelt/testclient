package nl.kadaster.geodatastore.cucumbertest;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.kadaster.geodatastore.JsonConverter;
import nl.kadaster.geodatastore.MetaData;
import nl.kadaster.geodatastore.TestClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bvpelt on 10/3/15.
 */
public class GeoDataStoreStepdefs {
    private static Logger logger = LoggerFactory.getLogger(GeoDataStoreStepdefs.class);
    //private static String host = "http://test1:password@ngr3.geocat.net";
    private static String host = "https://WPM:testtest@test.geodatastore.pdok.nl";
    private static String baseUrl = host + "/geonetwork/geodatastore/api";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseCodeListUrl = baseUrl + "/registry";
    private CloseableHttpResponse response;

    private TestClient testclient = null;
    private String lastIdentifier = null;

    @Given("^There is a testclient$")
    public void there_is_a_testclient() throws Throwable {
        logger.info("Create testclient");
        testclient = new TestClient();
        testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
    }

    @When("^I upload a random file$")
    public void i_upload_a_random_file() throws Throwable {
        logger.info("Upload random file");
        testclient.setAddRandomFile(true);
        testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        response = testclient.sendRequest(baseDataSetUrl, TestClient.HTTPPOST);
        if (null == response) {
            throw new Exception("No response object available");
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Error in upload random file");
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
                StringBuffer resultText = new StringBuffer(content);
                if (resultText.toString().length() > 0) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        MetaData md = new MetaData();
                        JsonConverter json = new JsonConverter();

                        json.loadString(resultText.toString());
                        lastIdentifier = json.getStringNode("identifier");
                        logger.info("Identifier: {}", lastIdentifier);
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

    @Given("^I have uploaded a random file and got the identifier of the uploaded dataset$")
    public void i_have_uploaded_a_random_file_and_got_the_identifier_of_the_uploaded_dataset() throws Throwable {
        if ((null == lastIdentifier) || (lastIdentifier.length() == 0)) {
            throw new Exception("No identifier found");
        }
    }

}
