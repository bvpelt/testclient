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

public class RegistrySteps {
    private static Logger logger = LoggerFactory.getLogger(RegistrySteps.class);

    @Autowired
    private TestClient tc;

    @Autowired
    private Configuration conf;

    private CloseableHttpResponse response = null;

    @Given("^There is a testclient$")
    public void thereIsATestclient() throws Throwable {
        logger.debug("Start: There is a testclient");

		/*
         * tc.setConnectTimeOut(conf.getConnectTimeOut());
		 * tc.setRequestTimeOut(conf.getRequestTimeOut());
		 * tc.setSocketTimeOut(conf.getSocketTimeOut());
		 */
        tc.addHeader("Accept", "application/json");

        if (conf.isUseproxy()) {
            tc.setProxy(conf.getProxyHost(), conf.getProxyPort());
        }
        Assert.assertNotNull(tc);
    }

    @When("^I ask for known registry services$")
    public void iAskForKnownCodelists() throws Throwable {
        logger.debug("Start: I ask for known registry services");

        String url = conf.getRegistries(false);
        response = tc.sendRequest(url, TestClient.HTTPGET);
        Assert.assertNotNull(response);
    }

    @Then("^I get a http success status$")
    public void iGetAHttpSuccessStatus() throws Throwable {
        logger.debug("Start: I get a http success status");

        // Assert.assertNotNull(response);
        Assert.assertEquals(200, tc.getStatusCode());
    }

    @Then("^I get a list of (\\d+) known registrie services with names \\'(.*)\\'$")
    public void iGetAListOfKnownRegistrieServices(int numServices, String names) throws Throwable {
        logger.debug("Start I get a list of {} known registrie services with names {}", numServices, names);

        String[] registryNames = names.split(", ");

        try {

            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();
                    RegistryServicesResponse myObjects = jc.toObject(tc.getResultText(),
                            RegistryServicesResponse.class);

                    for (String regName : registryNames) {
                        logger.debug("Checking if {} exists", regName);
                        Checks checks = new Checks();
                        boolean found = checks.nameInServiceList(regName, myObjects);
                        Assert.assertEquals("service: " + regName + " found", true, found);
                    }
                    Assert.assertEquals(numServices, myObjects.getRegistries().length);
                    logger.debug("Found objects: {}", myObjects.getRegistries().toString());
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

    @When("^I ask register service (.*) without filtering$")
    public void iAskRegisterServiceLicenseWithoutFiltering(String regServiceName) throws Throwable {
        logger.debug("Start: I ask register service {} without filtering", regServiceName);

        // set parameters
        tc.addHeader("Accept", "application/json");
        Assert.assertNotNull(tc);

        // get list of registries
        String url = conf.getRegistries(false);
        response = tc.sendRequest(url, TestClient.HTTPGET);
        Assert.assertNotNull(response);

        String testUri = findTestUri(regServiceName);

        // testUri found, so call that testUri
        if (testUri != null) {
            tc.addHeader("Accept", "application/json");

            logger.debug("Request service: {} on uri: {}", regServiceName, testUri);
            response = tc.sendRequest(testUri, TestClient.HTTPGET);
        } else {
            logger.error("No service url found for registry service {}", regServiceName);
        }
        Assert.assertNotNull(response);

    }

    @Then("^I get http status (\\d+)$")
    public void iGetHttpStatus(int status) throws Throwable {
        logger.debug("Start: I get http status {}", status);

        Assert.assertNotNull(response);
        Assert.assertEquals(status, tc.getStatusCode());
    }

    @When("^I ask register service (.*) with parameter (.*)$")
    public void iAskRegisterServiceWithParameter(String regServiceName, String parameters) throws Throwable {
        logger.debug("Start: I ask register service {} with parameter {}", regServiceName, parameters);

        // set parameters
        tc.addHeader("Accept", "application/json");
        Assert.assertNotNull(tc);

        // get list of registries
        String url = conf.getRegistries(false);
        response = tc.sendRequest(url, TestClient.HTTPGET);
        Assert.assertNotNull(response);

        String testUri = findTestUri(regServiceName);

        if (testUri != null) {
            // Not yet implemented, topic Codelist is implemented
            tc.addHeader("Accept", "application/json");
            testUri = testUri + "?" + parameters;
            logger.debug("Request service: {} with parameters: {} on uri: {}", regServiceName, parameters, testUri);
            response = tc.sendRequest(testUri, TestClient.HTTPGET);
        } else {
            logger.error("No service url found for registry service {}", regServiceName);
        }
        Assert.assertNotNull(response);
    }

    private String findTestUri(String regServiceName) throws Exception {
        logger.debug("find test uri in list {}", regServiceName);

        // find testUri for regServiceName by getting list of registrie services
        // and determine the url from list
        String testUri = null;
        try {
            if (tc.getStatusCode() == 200) {
                try {
                    JsonConverter jc = new JsonConverter();
                    RegistryServicesResponse myObjects = jc.toObject(tc.getResultText(),
                            RegistryServicesResponse.class);

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
                } catch (Exception e) {
                    logger.error("Error during conversion of json response: {}", e);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error extracting result");
        } finally {
            tc.closeSession();
        }
        return testUri;
    }

}
