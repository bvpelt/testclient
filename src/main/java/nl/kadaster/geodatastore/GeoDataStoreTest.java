package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class GeoDataStoreTest {
    private static Logger logger = LoggerFactory.getLogger(GeoDataStoreTest.class);
    private int failures = 0;
    private int tests = 0;
    private boolean verbose = true;

    public static void main(String[] args) {

        try {
            GeoDataStoreTest app = new GeoDataStoreTest();

            app.doTests();
        } catch (Exception e) {
            logger.error("App received error: ", e);
        }
    }

    private void doTests() throws Exception {
        logger.info("Start tests");

        TestClient testClient = new TestClient();
        testClient.setProxy("www-proxy.cs.kadaster.nl", 8082);

        failures += TestGetList(testClient);
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
    }

    /**
     * Test get list of known datasets
     *
     *
     * @param testclient
     * @return
     */
    private int TestGetList(final TestClient testclient) {
        int error = 0;
        String testName = "TestGetList";

        logger.info("Start test: {}", testName);

        try {
            CloseableHttpResponse response = testclient.sendRequest("https://WPM:testtest@test.geodatastore.pdok.nl/geonetwork/geodatastore/api/datasets?from=1&pageSize=8&sortBy=changeDate&sortOrder=desc&status=draft", TestClient.HTTPGET);
            //CloseableHttpResponse response = testclient.sendRequest("http://test1:password@ngr3.geocat.net/geonetwork/geodatastore/api/datasets?from=1&pageSize=8&sortBy=changeDate&sortOrder=desc&status=draft", TestClient.HTTPGET);
            //testclient.setAddRandomFile(true);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {}", testName);
        return error;
    }


    private int evaluateResult(String testName, CloseableHttpResponse response, int statusCode) {
        final String expectedProtocolVersion = "HTTP/1.1";
        final String protocolVersion = response.getProtocolVersion().toString();
        final int ontvangenStatusCode = response.getStatusLine().getStatusCode();

        int result = 0;

        if (response.getStatusLine().getStatusCode() != statusCode) {
            logger.error("Test: {} - received invalid statuscode: {} expected statuscode: {}", testName, Integer.toString(ontvangenStatusCode), statusCode);
            result++;
        }

        if (!protocolVersion.equals(expectedProtocolVersion)) {
            logger.error("Test: {} - received invalid protocolversion: {}  expected protocolversion: {}", testName, protocolVersion, expectedProtocolVersion);
            result++;
        }

        if (verbose) {
            HttpEntity entity = null;

            try {
                entity = response.getEntity();
                if (entity != null) {
                    String content = EntityUtils.toString(entity);
                    logger.info("Result size: {}, content: {}", content.length(), content);
                }
            } catch (Exception e) {
                logger.error("Error extracting result");
            }
        }


        return result;
    }

}