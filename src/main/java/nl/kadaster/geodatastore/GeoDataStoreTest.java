package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Hello world!
 */
public class GeoDataStoreTest {
    private static Logger logger = LoggerFactory.getLogger(GeoDataStoreTest.class);
    //private static String host = "http://test1:password@ngr3.geocat.net";
    private static String host = "https://WPM:testtest@test.geodatastore.pdok.nl";
    private static String baseUrl = host + "/geonetwork/geodatastore/api";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseCodeListUrl = baseUrl + "/registry";
    private int failures = 0;
    private int tests = 0;
    private boolean verbose = true;
    private StringBuffer resultText = null;
    private String lastIdentifier;

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

        // Good situations
        // The order of the tests is vital, don't change

        // Get available codelists
        failures += TestGetCodeLists(testClient);
        tests++;

        // Upload file
        failures += TestPost01(testClient);
        tests++;

        // Add metadata
        failures += TestPostAddMetaData(testClient);
        tests++;

        // Get list uploaded datasets 1-2 entries
        failures += TestGetList1_2(testClient);
        tests++;

        // Get list uploaded datasets 1-20 entries
        failures += TestGetList1_20(testClient);
        tests++;

        // Delete dataset
        failures += TestDeleteDataset(testClient);
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
    }

    /**
     * Test get list of available code lists
     *
     * @param testclient
     * @return
     */
    private int TestGetCodeLists(final TestClient testclient) {
        int error = 0;
        String testName = "TestGetCodeLists";

        logger.info("Start test: {}", testName);

        String url = baseCodeListUrl;
        try {
            testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            CloseableHttpResponse response = testclient.sendRequest(url, TestClient.HTTPGET);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
        return error;
    }


    /**
     * Test upload new dataset
     *
     * @param testclient
     * @return
     */
    private int TestPost01(final TestClient testclient) {
        int error = 0;
        String testName = "TestPost01";

        logger.info("Start test: {}", testName);

        try {
            testclient.setAddRandomFile(true);
            testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            CloseableHttpResponse response = testclient.sendRequest(baseDataSetUrl, TestClient.HTTPPOST);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
        return error;
    }

    private MetaData generateMetaData(final String jsonString) {
        MetaData md = new MetaData();
        JsonConverter json = new JsonConverter();

        json.loadString(jsonString);
        md.setTitle("TRIAL databestand");
        md.setSummary("TRIAL databestand to make sure update metadata works");
        md.setKeywords("TRIAL databestand, Geodatastore");
        md.setIdentifier(json.getStringNode("identifier"));

        return md;
    }


    private File writeJsonFile(final String fileName, final String jsonString) {

        File file = new File(fileName);
        try {
            // always overwrite file
            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(jsonString);
            bw.close();

        } catch (Exception e) {
            logger.error("Couldnot create file: {}", fileName, e);
        }
        return file;
    }

    /**
     * Test add metadata to previously created dataset
     */
    private int TestPostAddMetaData(final TestClient testclient) {
        int error = 0;
        String testName = "TestPostAddMetaData";

        logger.info("Start test: {}", testName);

        try {
            String jsonString;
            String fileName = "somefile.txt";
            // generate metadata based on result from previous call
            MetaData md = generateMetaData(resultText.toString());
            JsonConverter jc = new JsonConverter();
            jsonString = jc.getObjectJson(md);
            writeJsonFile(fileName, jsonString);
            testclient.getFileEntity(fileName);
            testclient.setPublish(false);

            String url = baseDataSetUrl + "/" + md.getIdentifier();
            testclient.getFileEntity(fileName);
            testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");

            CloseableHttpResponse response = testclient.sendRequest(url, TestClient.HTTPPOST);


            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
        return error;
    }

    /**
     * Test get list of known datasets 1-2 entries
     *
     * @param testclient
     * @return
     */
    private int TestGetList1_2(final TestClient testclient) {
        int error = 0;
        String testName = "TestGetList";

        logger.info("Start test: {}", testName);

        String url = baseDataSetUrl + "?from=1&pageSize=2&sortBy=changeDate&sortOrder=desc&status=draft";
        try {
            testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            CloseableHttpResponse response = testclient.sendRequest(url, TestClient.HTTPGET);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
        return error;
    }

    /**
     * Test get list of known datasets 1-20 entries
     *
     * @param testclient
     * @return
     */
    private int TestGetList1_20(final TestClient testclient) {
        int error = 0;
        String testName = "TestGetList1_20";

        logger.info("Start test: {}", testName);

        String url = baseDataSetUrl + "?from=1&pageSize=20&sortBy=changeDate&sortOrder=desc&status=draft";
        try {
            testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            CloseableHttpResponse response = testclient.sendRequest(url, TestClient.HTTPGET);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
        return error;
    }

    /**
     * Test delete dataset
     *
     * @param testclient
     * @return
     */
    private int TestDeleteDataset(final TestClient testclient) {
        int error = 0;
        String testName = "TestDeleteDataset";

        logger.info("Start test: {}", testName);

        try {
            String jsonString;
            String fileName = "somefile.txt";
            /*
            // generate metadata based on result from previous call
            MetaData md = generateMetaData(resultText.toString());
            */
            String url;
            //url = baseDataSetUrl + "/"+ md.getIdentifier();
            url = baseDataSetUrl + "/" + lastIdentifier;

            CloseableHttpResponse response = testclient.sendRequest(url, TestClient.HTTPDELETE);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
        } finally {
            testclient.closeSession();
        }
        logger.info("End   test: {} with {}", testName, ((error == 0) ? "success" : "failure"));
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
                    resultText = new StringBuffer(content);
                    if (resultText.toString().length() > 0) {
                        if (response.getStatusLine().getStatusCode() == 200) {
                            MetaData md = generateMetaData(resultText.toString());
                            lastIdentifier = md.getIdentifier();
                        }
                    }
                    logger.info("Result size: {}, content: {}", content.length(), content);
                }
            } catch (Exception e) {
                logger.error("Error extracting result");
            }
        }

        return result;
    }

}