package nl.kadaster.geodatastore.cucumbertest;

/**
 * Created by bvpelt on 10/3/15.
 */


import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.kadaster.geodatastore.TestClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PDokStepdefs {
    private static Logger logger = LoggerFactory.getLogger(PDokStepdefs.class);

    private CloseableHttpResponse response = null;
    private List<String> servicenames = null;
    private int numberError = 0;

    @Given("^The following wfs services: (.*)$")
    public void the_following_wfs_services_bag_brpgewaspercelen_beschermdenatuurmonumenten(List<String> servicenames)
            throws Throwable {
        for (String service : servicenames) {

            logger.info("Calling service {}", service);

        }
        this.servicenames = servicenames;
    }

    @When("^I ask wfs get capabilities secure$")
    public void i_ask_wfs_get_capabilities_secure() throws Throwable {
        for (String service : servicenames) {
            TestClient testclient = new TestClient();
            String url = "https://geodata.nationaalgeoregister.nl/" + service + "/wfs?request=GetCapabilities";
            logger.info("Calling service {} with url {}", service, url);
            response = testclient.sendRequest(url, TestClient.HTTPGET);
            if (response == null) {
                numberError++;
            } else if (response.getStatusLine().getStatusCode() != 200) {
                numberError++;
            }
        }
    }

    @When("^I ask wfs get capabilities not secure$")
    public void i_ask_wfs_get_capabilities_not_secure() throws Throwable {
        for (String service : servicenames) {
            TestClient testclient = new TestClient();
            String url = "http://geodata.nationaalgeoregister.nl/" + service + "/wfs?request=GetCapabilities";
            logger.info("Calling service {} with url {}", service, url);
            response = testclient.sendRequest(url, TestClient.HTTPGET);
            if (response.getStatusLine().getStatusCode() != 200) {
                numberError++;
            }
        }

    }

    @Then("^I get correct answer$")
    public void i_get_correct_answer() throws Throwable {
        logger.error("Number error: {}", numberError);
        if (numberError > 0) {
            throw new Error("At least one error occured");
        }
    }

}
