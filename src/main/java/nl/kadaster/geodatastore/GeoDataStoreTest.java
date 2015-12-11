package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
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
    private static String baseUrl = host + "/api/v1";
    private static String baseDataSetUrl = baseUrl + "/dataset";
    private static String baseCodeListUrl = baseUrl + "/registry";
    private int failures = 0;
    private int tests = 0;
    private boolean useProxy = false;
    private boolean verbose = true;
    private StringBuffer resultText = null;
    private String lastIdentifier;
    private MetaDataResponse mdresponse;

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
        if (useProxy) {
            testClient.setProxy("www-proxy.cs.kadaster.nl", 8082);
        }
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
            
            File randomFile = getRandomFile();
            
            testclient.addPostFile("dataset", randomFile);
            
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

    private MetaDataResponse generateMetaData(final String jsonString) {
        MetaDataResponse md = new MetaDataResponse();
        JsonConverter json = new JsonConverter();

        json.loadString(jsonString);

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
            boolean publish = false;
            
            // generate metadata based on result from previous call
            MetaDataRequest mdrequest = new MetaDataRequest();
            mdrequest.setTitle("TEST METADATA TITLE");
            mdrequest.setSummary("TEST METADATA SUMMARY this is the summary of the test metadata");
            mdrequest.addKeyword("TEST");
            mdrequest.addKeyword("METADATA");
            mdrequest.addTopicCategorie("Gezondheid");
            mdrequest.addTopicCategorie("Grenzen");
            //mdrequest.setLocation("Apeldoorn");
            mdrequest.setLineage("Lineage");
            mdrequest.setLicense("Public Domain");
            mdrequest.setResolution(1000);
            JsonConverter jc = new JsonConverter();
            jsonString = jc.getObjectJson(mdrequest);
    
            testclient.addPostString("metadata", jsonString, ContentType.APPLICATION_JSON);
            testclient.addPostString("publish", Boolean.toString(publish));
            
            String url = baseDataSetUrl + "/" + mdresponse.getIdentifier();
            
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
        
            String url;
            
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
                            mdresponse = generateMetaData(resultText.toString());
                            lastIdentifier = mdresponse.getIdentifier();
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