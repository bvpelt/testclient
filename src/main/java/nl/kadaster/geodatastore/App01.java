package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.Charset;

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

        TC tc = new TC();
        tc.setProxy("www-proxy.cs.kadaster.nl", 8082);
/*
        failures += t01();
        tests++;
*/
        failures += Test01(tc);
        tests++;

        failures += Test02(tc);
        tests++;

        failures += Test03(tc);
        tests++;

        logger.info("End   tests, executed: {} with {} failures", tests, failures);
    }

    private int t01() {
        int result = 0;
        String scheme;
        String authority;
        String host;
        String path;
        String query;
        String fragment;

        String url = "https://test.geodatastore.pdok.nl/geonetwork/geodatastore/api/dataset";
        URI uri = URI.create(url);

        scheme = uri.getScheme();
        authority = uri.getAuthority();
        host = uri.getHost();
        path = uri.getPath();
        query = uri.getQuery();
        fragment = uri.getFragment();

        logger.info("uri {}\nscheme {}\n authority {}\n host {}\n path {}\n query {}\n fragment {}", url, scheme, authority, host, path, query, fragment);

        url = "https://jaap@test:test.geodatastore.pdok.nl:448/geonetwork/geodatastore/api/dataset";
        uri = URI.create(url);

        scheme = uri.getScheme();
        host = uri.getHost();
        path = uri.getPath();
        authority = uri.getAuthority();
        query = uri.getQuery();
        fragment = uri.getFragment();

        // if authority
        // split userinfo:host:port,
        // then split userinfo into username password

        logger.info("uri {}\nscheme {}\n authority {}\n port {}\n host {}\n path {}\n query {} \n fragment", url, scheme, authority, uri.getPort(), host, path, query, fragment);

        url = "https://jaap@test:test.geodatastore.pdok.nl:448/geonetwork/geodatastore/api/dataset?request=WMS&version=1.1.0&operation=getDescription";
        uri = URI.create(url);

        scheme = uri.getScheme();
        host = uri.getHost();
        path = uri.getPath();
        authority = uri.getAuthority();
        query = uri.getQuery();
        fragment = uri.getFragment();

        // if authority
        // split userinfo:host:port,
        // then split userinfo into username password
        String user = "";
        String pwd = "";
        String port = "";

        if ((authority != null) && (authority.length()>0)) {
            String[] parts = authority.split(":");
            String part1;
            String part2;
            String part3;

            part1 = parts[0];
            if (part1.contains("@")) { // username password specified (optional)
                String[] pwdparts = part1.split("@");
                user= pwdparts[0];
                pwd = pwdparts[1];

                if (parts.length > 1) {
                    host = parts[1];
                }
                if (parts.length > 2) {
                    port = parts[2];
                }
            } else {
                host = part1;
                if (parts.length > 1) {
                    port = parts[1];
                }
            }
        }

        logger.info("uri {}\nscheme {}\n authority {}\n user {}\n pwd {} \n port {}\n host {}\n path {}\n query {} \n fragment", url, scheme, authority, user, pwd, port, host, path, query, fragment);


        return result;
    }

    private int Test01(final TC testclient) {
        int error = 0;
        String testName = "Test01";

        logger.info("Start test: {}", testName);

        try {
            CloseableHttpResponse response = testclient.sendRequest("https://test.geodatastore.pdok.nl/geonetwork/geodatastore/api/dataset", TC.HTTPPOST);
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
            CloseableHttpResponse response = testclient.sendRequest("https://WPM:testtest@test.geodatastore.pdok.nl/geonetwork/geodatastore/api/dataset", TC.HTTPPOST);
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
            CloseableHttpResponse response = testclient.sendRequest("http://www.pdok.nl/", TC.HTTPGET);
            error = evaluateResult(testName, response, 200);
        } catch (Exception e) {
            error += 1;
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
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    long len = entity.getContentLength();
                    if (len > 0) {
                        Charset defaultCharset = Charset.forName("utf-8");
                        logger.info("Result: {}", EntityUtils.toString(entity,defaultCharset));
                    } else {

                    }
                }
            } catch (Exception e) {
                logger.error("Error while processing content", e);
            }
        }

        return result;
    }

}