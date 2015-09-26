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

        App01 app = new App01();

        app.doTests();
    }

    private void doTests() {
        logger.info("Start tests");

        TC tc = new TC();

        failures += Test01(tc);
        tests++;

        failures += Test02(tc);
        tests++;

        failures += Test03(tc);
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
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
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    long len = entity.getContentLength();
                    if (len > 0 && len < 2048) {
                        logger.info("Result: {}", EntityUtils.toString(entity));
                    } else {

                    }
                }
            } catch (Exception e) {
                logger.error("Error while processing content", e);
            }
        }

        return result;
    }

    private int Test01(final TC testclient) {
        int error = 0;
        String testName = "Test01";

        logger.info("Start test: {}", testName);

        try {
            CloseableHttpResponse response = testclient.sendPostRequest("https", "test.geodatastore.pdok.nl", 443, "/geonetwork/geodatastore/api/dataset");
            error = evaluateResult(testName, response, 401);
        } catch (Exception e) {
            error += 1;
        }
        logger.info("End   test: {}", testName);
        return error;
    }

    private int Test02(final TC testclient) {
        int error = 0;
        String testName = "Test02";

        logger.info("Start test: {}", testName);
        try {
            CloseableHttpResponse response = testclient.sendPostRequest("https", "test.geodatastore.pdok.nl", 443, "/geonetwork/geodatastore/api/dataset", "admin", "testtest");
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        }
        logger.info("End   test: {}", testName);
        return error;
    }

    private int Test03(final TC testclient) {
        int error = 0;
        String testName = "Test03";

        logger.info("Start test: {}", testName);
        testclient.setUseBasicAuthentication(false);
        try {
            CloseableHttpResponse response = testclient.sendGetRequest("http", "www.nu.nl", 80, "/");
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        }
        logger.info("End   test: {}", testName);
        return error;
    }
}