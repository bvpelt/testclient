package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App01 {
    private static Logger logger = LoggerFactory.getLogger(App01.class);
    private int failures = 0;
    private int tests = 0;
    private boolean verbose = true;

    public static void main(String[] args) {

        try {
            App01 app = new App01();

            app.doTests();
        } catch (Exception e) {
            logger.error("App received error: ", e);
        }
    }

    private void doTests() throws Exception {
        logger.info("Start tests");

        TestClient testClient = new TestClient();
        testClient.setProxy("www-proxy.cs.kadaster.nl", 8082);

        failures += Test01(testClient);
        tests++;

        failures += Test02(testClient);
        tests++;

        failures += Test03(testClient);
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
    }

    private int Test01(final TestClient testclient) {
        int error = 0;
        String testName = "Test01";

        logger.info("Start test: {}", testName);

        try {
            CloseableHttpResponse response = testclient.sendRequest("https://test.geodatastore.pdok.nl/geonetwork/geodatastore/api/dataset", TestClient.HTTPPOST);
            error = evaluateResult(testName, response, 401);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {}", testName);
        return error;
    }

    private int Test02(final TestClient testclient) {
        int error = 0;
        String testName = "Test02";

        logger.info("Start test: {}", testName);
        try {
            CloseableHttpResponse response = testclient.sendRequest("https://WPM:testtest@test.geodatastore.pdok.nl/geonetwork/geodatastore/api/dataset", TestClient.HTTPPOST);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {}", testName);
        return error;
    }

    private int Test03(final TestClient testclient) {
        int error = 0;
        String testName = "Test03";

        logger.info("Start test: {}", testName);
        testclient.setUseBasicAuthentication(false);
        try {
            CloseableHttpResponse response = testclient.sendRequest("http://www.nu.nl/", TestClient.HTTPGET);
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