package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ProtocolVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

public class TestClient {
    private static String HTTPS = "https";

    private static Logger logger = LoggerFactory.getLogger(TestClient.class);

    // The Parameters of the test site
    private String scheme;
    private int port;
    private String host;
    private String path;
    private String username;
    private String password;

    // The proxy settings (specific to company outbound internet access)
    private String proxyHost;
    private int proxyPort;

    // The keystore password (used for TLS connections and proxy)
    private String keystorepwd;

    // The connection parameters
    private int socketTimeOut; // milliseconds
    private int connectTimeOut; // milliseconds
    private int requestTimeOut; // milliseconds

    // Optional parameters
    private boolean useProxy;
    private boolean useBasicAuth;
    private boolean verbose;

    /**
     * Create and initialize the test client
     */
    public TestClient() {
        // The Parameters of the test site
        scheme = HTTPS;
        port = 443;
        host = "ngr3.geocat.net";
        path = "/geonetwork/geodatastore/api/dataset";
        username = "test1";
        password = "password";

        // The proxy settings (specific to company outbound internet access)
        proxyHost = "www-proxy.cs.kadaster.nl";
        proxyPort = 8082;

        // The keystore password (used for TLS connections and proxy)
        keystorepwd = "geodatastore";

        // The connection parameters
        socketTimeOut = 5000; // milliseconds
        connectTimeOut = 5000; // milliseconds
        requestTimeOut = 5000; // milliseconds

        // Optional parameters
        useProxy = true;
        useBasicAuth = true;
        verbose = true;

        // Overwrite parameters from propertie file test.properties on the classpath
        initialize();
    }

    /**
     * getRequestConfig based on specified parameters
     *
     * @return a valid RequestConfig
     */
    private RequestConfig getRequestConfig() {

        RequestConfig dcNoAuth = RequestConfig.custom()
                .setSocketTimeout(socketTimeOut)
                .setConnectTimeout(connectTimeOut)
                .setConnectionRequestTimeout(requestTimeOut)
                .build();

        RequestConfig rc = RequestConfig.copy(dcNoAuth)
                .build();

        return rc;
    }

    /**
     * Get a parameterized http client, based on usage off https and useProxy setting
     *
     * @return a valid CloableHttpClient based on the scheme and useProxy setting
     */
    private CloseableHttpClient getHttpClient(CredentialsProvider credsProvider) {
        CloseableHttpClient httpclient = null;
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);

        try {
            logger.debug("Creating httpclient with schema {} and useProxy {} and useBasicAuth {}", scheme, useProxy, useBasicAuth);

            if (scheme.equals(HTTPS)) {
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(new File("proxykeystore.jks"), keystorepwd.toCharArray(),
                                new TrustSelfSignedStrategy())
                        .build();

                // Allow TLSv1 protocol only
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslcontext,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());


                // Only if scheme is https and use Proxy the certificate store should be loaded!!!
                if (scheme.equals(HTTPS) && useProxy && useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .setProxy(proxy)
                            .setDefaultCredentialsProvider(credsProvider)
                            .build();
                }

                if (scheme.equals(HTTPS) && useProxy && !useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .setProxy(proxy)
                            .build();
                }

                if (scheme.equals(HTTPS) && !useProxy && useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .setDefaultCredentialsProvider(credsProvider)
                            .build();
                }

                if (scheme.equals(HTTPS) && !useProxy && !useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .build();
                }

            } else {
                // scheme not equal to HTTPS
                if (useProxy && useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setProxy(proxy)
                            .setDefaultCredentialsProvider(credsProvider)
                            .build();
                }

                if (useProxy && !useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setProxy(proxy)
                            .build();
                }

                if (!useProxy && useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .setDefaultCredentialsProvider(credsProvider)
                            .build();
                }

                if (!useProxy && !useBasicAuth) {
                    httpclient = HttpClients.custom()
                            .build();
                }
            }

        } catch (Exception e) {
            logger.error("Error creating http client", e);
        }

        return httpclient;
    }

    /**
     * executeRequestNoAuth - Execute a http post request
     *
     * @param httpPost
     * @return
     */
    /*
    private CloseableHttpResponse executeRequestNoAuth(HttpPost httpPost) {
        String keystorepwd = "geodatastore";
        CredentialsProvider credentialsProvider;
        CloseableHttpResponse response = null;

        HttpHost target = new HttpHost(host, port, scheme);

        try {
            credentialsProvider = new BasicCredentialsProvider();
            CloseableHttpClient httpclient = getHttpClient(credentialsProvider);

            logger.info("Sending request to: {}", httpPost.toString());

            response = httpclient.execute(target, httpPost);

            ProtocolVersion protocolVersion = response.getProtocolVersion();
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Protocol version: {} status code {}", protocolVersion.toString(), Integer.toString(statusCode));
        } catch (Exception e) {
            logger.error("Error during execute request", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {

            }
        }
        return response;
    }
*/


    /**
     * getLocalContext - Create a context for basic authentication
     *
     * @param target
     * @return a HttpClientContext
     */
    private HttpClientContext getLocalContext(CredentialsProvider credsProvider, final HttpHost target) {
        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(username, password));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();

        // Generate BASIC scheme object and add it to the local
        // auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(target, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        return localContext;
    }

    /**
     * executeRequest
     *
     * @param httpRequest
     * @return Do close response on exit, but do not deassing response, it can still be read out for results
     */

    private CloseableHttpResponse executeRequest(HttpRequestBase httpRequest) {
        CloseableHttpResponse response = null;
        HttpHost target = new HttpHost(host, port, scheme);
        HttpClientContext localContext = null;
        CredentialsProvider credentialsProvider;

        try {
            credentialsProvider = new BasicCredentialsProvider();


            // Create and fill a credentialsProvider at this place, do not move this !!1
            if (useBasicAuth) {
                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(target.getHostName(), target.getPort()),
                        new UsernamePasswordCredentials(username, password));

                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();

                // Generate BASIC scheme object and add it to the local
                // auth cache
                BasicScheme basicAuth = new BasicScheme();
                authCache.put(target, basicAuth);

                // Add AuthCache to the execution context
                localContext = HttpClientContext.create();
                localContext.setAuthCache(authCache);
            }

            CloseableHttpClient httpclient = getHttpClient(credentialsProvider);

            logger.info("Sending request to: {}", httpRequest.toString());

            if (useBasicAuth) {
                response = httpclient.execute(target, httpRequest, localContext);
            } else {
                response = httpclient.execute(target, httpRequest);
            }

            response = httpclient.execute(target, httpRequest, localContext);
            ProtocolVersion protocolVersion = response.getProtocolVersion();
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Protocol version: {} status code {}", protocolVersion.toString(), Integer.toString(statusCode));
        } catch (Exception e) {
            logger.error("Error during execute request", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("Error closing connection", e);
            }
        }
        return response;
    }



    /**
     * executeRequest
     *
     * @param httpPost
     * @return Do close response on exit, but do not deassing response, it can still be read out for results
     */
    private CloseableHttpResponse executeRequest01(HttpPost httpPost) {
        CloseableHttpResponse response = null;
        HttpHost target = new HttpHost(host, port, scheme);
        HttpClientContext localContext = null;
        CredentialsProvider credentialsProvider;

        try {
            credentialsProvider = new BasicCredentialsProvider();

            // Create and fill a credentialsProvider at this place, do not move this !!1
            if (useBasicAuth) {

                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(target.getHostName(), target.getPort()),
                        new UsernamePasswordCredentials(username, password));

                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();

                // Generate BASIC scheme object and add it to the local
                // auth cache
                BasicScheme basicAuth = new BasicScheme();
                authCache.put(target, basicAuth);

                // Add AuthCache to the execution context
                localContext = HttpClientContext.create();
                localContext.setAuthCache(authCache);
            }

            CloseableHttpClient httpclient = getHttpClient(credentialsProvider);

            logger.info("Sending request to: {} ", httpPost.getRequestLine());

            if (useBasicAuth) {
                response = httpclient.execute(target, httpPost, localContext);
            } else {
                response = httpclient.execute(target, httpPost);
            }
            ProtocolVersion protocolVersion = response.getProtocolVersion();
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Protocol version: {} status code {}", protocolVersion.toString(), Integer.toString(statusCode));
        } catch (Exception e) {
            logger.error("Error during execute request", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("Error closing connection", e);
            }
        }
        return response;
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

    // Test rest call without basic authentication http://jira.so.kadaster.nl/browse/PDOK-1649
    //curl -v --http1.0 --proxy http://www-proxy.cs.kadaster.nl:8082  http://ngr3.geocat.net/geonetwork/geodatastore/api/dataset  -H "Accept: application/json, text/javascript, */*; q=0.01" --form name=test.txt --form dataset=@test.txt
    public int Test01() {
        // build request
        HttpPost httpPost = null;
        int result = 0;
        String testName = "Test01";
        CloseableHttpResponse response = null;

        logger.info("Start test {}", testName);

        try {
            URI uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .build();

            httpPost = new HttpPost(uri);
            httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");

            FileEntity entity = getFile();
            httpPost.setEntity(entity);

            boolean orgBasicConfig = isUseBasicAuth();
            setUseBasicAuth(false);
            logger.info("Explicitly set basic authentication off");

            // do not use basic authentication!!
            httpPost.setConfig(getRequestConfig());

            // execute erquest

            response = executeRequest(httpPost);

            // evaludate result
            result = evaluateResult(testName, response, 401);

            setUseBasicAuth(orgBasicConfig);

            if (result == 0) {
                logger.info("Test: {}  succeeded", testName);
            } else {
                logger.error("Test: {}  failed on {}", testName, Integer.toString(result));
            }
        } catch (Exception e) {
            logger.error("Error building request", e);
        }
        logger.info("End   test {}", testName);

        return result;
    }

    // Test rest call ophalen codelist http://jira.so.kadaster.nl/browse/PDOK-1659
    // $ curl -v --http1.1 -X GET --proxy http://www-proxy.cs.kadaster.nl:8082 --user test1:password http://ngr3.geocat.net/geonetwork/geodatastore/registry  -H "Accept: application/json, text/javascript, */*; q=0.01"
    public int Test02() {
        // build request
        HttpPost httpPost = null;
        int result = 0;
        String testName = "Test02";
        CloseableHttpResponse response = null;

        logger.info("Start test {}", testName);

        try {
            String path = "/geonetwork/geodatastore/registry";

            URI uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .build();

            httpPost = new HttpPost(uri);
            httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");

            // Request configuration can be overridden at the request level.
            // They will take precedence over the one set at the client level.
            httpPost.setConfig(getRequestConfig());

            FileEntity entity = getFile();
            httpPost.setEntity(entity);

            // execute erquest
            response = executeRequest(httpPost);

            // evaludate result
            result = evaluateResult(testName, response, 200);

            if (result == 0) {
                logger.info("Test: {}  succeeded", testName);
            } else {
                logger.error("Test: {}  failed on {}", testName, Integer.toString(result));
            }
        } catch (Exception e) {
            logger.error("Error building request", e);
        }
        logger.info("End   test {}", testName);

        return result;
    }


    // Test rest call ophalen codelist http://jira.so.kadaster.nl/browse/PDOK-1648
    // curl -v --http1.0 --proxy http://www-proxy.cs.kadaster.nl:8082 --user test1:password1234 http://ngr3.geocat.net/geonetwork/geodatastore/api/dataset  -H "Accept: application/json, text/javascript, */*; q=0.01" --form name=test.txt --form dataset=@test.txt
    public int Test03() {
        // build request
        HttpPost httpPost = null;
        int result = 0;
        String testName = "Test03";
        CloseableHttpResponse response = null;

        logger.info("Start test {}", testName);

        try {
            URI uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .build();

            httpPost = new HttpPost(uri);
            httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");

            FileEntity entity = getFile();
            httpPost.setEntity(entity);

            httpPost.setConfig(getRequestConfig());

            // execute erquest
            response = executeRequest(httpPost);

            // evaludate result
            result = evaluateResult("Test03", response, 200);
            if (result == 0) {
                logger.info("Test: {}  succeeded", testName);
            } else {
                logger.error("Test: {}  failed on {}", testName, Integer.toString(result));
            }
        } catch (Exception e) {
            logger.error("Error building request", e);
        }
        logger.info("End   test {}", testName);

        return result;
    }

    private FileEntity getFile() {
        String testFile = "somefile.txt";
        File file = new File(testFile);
        FileEntity entity = null;

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

            entity = new FileEntity(file,
                    ContentType.create("text/plain", "UTF-8"));
        } catch (Exception e) {
            logger.error("Couldnot create file: {}", testFile, e);
        }

        return entity;
    }

    private int getIntParameter(final Properties p, final String paramName, final int def) {
        String s;
        int i;
        int result = def;

        s = p.getProperty(paramName);
        if (s != null) {
            try {
                i = Integer.parseInt(s);
                if (i != 0) {
                    result = i;
                }
            } catch (NumberFormatException e) {
                logger.warn("Error parsing parameter {}: {}, using the default value: {}", paramName, s, def);
            }
        }
        return result;
    }

    private String getStringProperty(final Properties p, final String name, final String def) {
        String s = null;
        s = p.getProperty("host");
        if (s == null) {
            s = def;
        }
        return s;
    }

    private void initialize() {
        // overwrite values based on property file
        Properties p = null;

        try {
            p = loadProperties();

            String s;
            int i;
            boolean b;

            // scheme
            scheme = getStringProperty(p, "scheme", scheme);

            // port
            port = getIntParameter(p, "port", port);

            // host
            host = getStringProperty(p, "host", host);

            // path
            path = getStringProperty(p, "path", path);

            // username
            username = getStringProperty(p, "username", username);

            // proxyHost = null;
            proxyHost = getStringProperty(p, "proxyHost", proxyHost);

            // proxyPort = 0;
            proxyPort = getIntParameter(p, "proxyPort", proxyPort);

            // The keystore password (used for TLS connections and proxy)
            // keystorepwd = null;
            keystorepwd = getStringProperty(p, "keystorepwd", keystorepwd);

            // The connection parameters
            // socketTimeOut = 0;
            socketTimeOut = getIntParameter(p, "socketTimeOut", socketTimeOut);

            // connectTimeOut = 0;
            connectTimeOut = getIntParameter(p, "connectTimeOut", connectTimeOut);

            // requestTimeOut = 0;
            requestTimeOut = getIntParameter(p, "requestTimeOut", requestTimeOut);

            // Optional parameters
            // useProxy = true;
            s = p.getProperty("useProxy");
            if (s != null) {
                b = Boolean.parseBoolean(s);
                useProxy = b;
            }
            // useBasicAuth = true;
            s = p.getProperty("useBasicAuth");
            if (s != null) {
                b = Boolean.parseBoolean(s);
                useBasicAuth = b;
            }
            // verbose = true;
            s = p.getProperty("verbose");
            if (s != null) {
                b = Boolean.parseBoolean(s);
                verbose = b;
            }
        } catch (Exception e) {
            logger.error("Problem loading properties", e);
        }
    }


    public Properties loadProperties() throws Exception {
        Properties properties = null;
        InputStream stream = null;
        try {
            properties = new Properties();
            stream = TestClient.class.getClassLoader().getResourceAsStream("test.properties");
            properties.load(stream);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                logger.error("Error closing stream", e);
            }
        }
        return properties;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getKeystorepwd() {
        return keystorepwd;
    }

    public void setKeystorepwd(final String keystorepwd) {
        this.keystorepwd = keystorepwd;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(final int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(final int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(final int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(final boolean useProxy) {
        this.useProxy = useProxy;
    }

    public boolean isUseBasicAuth() {
        return useBasicAuth;
    }

    public void setUseBasicAuth(final boolean useBasicAuth) {
        this.useBasicAuth = useBasicAuth;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
}