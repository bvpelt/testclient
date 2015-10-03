package nl.kadaster.geodatastore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by bvpelt on 9/26/15.
 */
public class TestClient04 {
    // Public accessable constants
    public static String HTTPS = "https";
    public static String HTTPGET = "GET";
    public static String HTTPPOST = "POST";
    public static String HTTPDELETE = "DELETE";

    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(TestClient04.class);
    // Internal constants
    private static int HTTPPORT = 80;
    private static int HTTPSPORT = 443;

    // Local private variable with default values
    private HttpHost proxy = null;

    // option basic authentication
    private boolean useBasicAuthentication = false;
    private String username;
    private String password;
    // map with http headers
    private HashMap<String, String> headers = null;
    // Private contexts for basic authentication
    private CredentialsProvider credentialsProvider = null;
    private HttpClientContext localContext = null;
    // optional proxy settings
    private boolean useProxy = false;
    private String proxyHost;
    private int proxyPort;
    // option to add a file
    private boolean addRandomFile = false;
    private boolean addFile = false;
    private FileBody fileEntity;

    // option publish
    private Boolean publish = null;
    private StringBody pubEntity;

    // The keystore password (used for TLS connections and proxy)
    private String keystorepwd = "geodatastore";

    // http request parameters in seconds
    private int socketTimeOut = 5000;
    private int connectTimeOut = 5000;
    private int requestTimeOut = 5000;

    private HttpHost target = null;
    private CloseableHttpResponse response = null;

    public TestClient04() {
        // Initialize to known values
        proxy = null;

        // option basic authentication
        useBasicAuthentication = false;
        username = "";
        password = "";
        // Private contexts for basic authentication
        credentialsProvider = null;
        localContext = null;
        // optional proxy settings
        useProxy = false;
        proxyHost = null;
        proxyPort = 0;
        // optional number of headers
        headers = null;
        // option to add a file
        addRandomFile = false;

        // The keystore password (used for TLS connections and proxy)
        keystorepwd = "geodatastore";

        // http request parameters in seconds
        socketTimeOut = 5000;
        connectTimeOut = 5000;
        requestTimeOut = 5000;

        target = null;
        response = null;
    }

    /**
     * Setup proxy for the test client
     *
     * @param proxyHost the hostname of the proxy
     * @param proxyPort the portnumber of the proxy
     * @throws Exception if either hostname or proxy is not specified proxy can't be specified
     */
    public void setProxy(final String proxyHost, final int proxyPort) throws Exception {
        if ((proxyHost == null) || (proxyHost.length() == 0) || (proxyPort == 0)) {
            throw new Exception("Adding proxy requires a proxy hostname and a proxy port number");
        }
        proxy = new HttpHost(proxyHost, proxyPort);
        useProxy = true;
    }

    public void addHeader(final String key, final String value) {
        if (null == headers) {
            headers = new HashMap<String, String>();
        }
        String curValue = headers.get(key);

        if ((curValue != null) && (curValue.length() > 0)) {
            logger.error("Header with name: {} - value: {} already found new value: {}", key, curValue, value);
        }

        headers.put(key, value);
    }

    /**
     * Send the specified http request
     *
     * @param url    the requested url
     * @param method the request http method (GET|POST)
     * @return a response
     * @throws Exception if anything failes
     */
    public CloseableHttpResponse sendRequest(final String url, final String method) throws Exception {
        response = null;
        target = null;

        try {
            if (!((method.toUpperCase().equals(HTTPGET)) || (method.toUpperCase().equals(HTTPPOST) || (method.toUpperCase().equals(HTTPDELETE))))) {
                throw new Exception("Unknown and unsupported method in url");
            }
            URI uri = URI.create(url);

            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            String authority = uri.getAuthority();
            String query = uri.getQuery();
            String fragment = uri.getFragment();

            // if authority
            // split userinfo:host:port,
            // then split userinfo into username password
            String user = "";
            String pwd = "";
            String port = "";
            int iport = 0;

            //
            // Generic definition of authority  user:password@host:port
            // determine if there is a "@" in the authority
            if ((authority != null) && (authority.length() > 0)) {
                String[] parts = authority.split("@");
                String part1;

                part1 = parts[0];
                if (authority.contains("@")) { // username password specified (optional)
                    String[] pwdparts = parts[0].split(":");
                    user = pwdparts[0];
                    if (pwdparts.length > 1) {
                        pwd = pwdparts[1];
                    }

                    if ((user == null) || (user.length() == 0) || (pwd == null) || (pwd.length() == 0)) {
                        throw new Exception("username password expected, none specified");
                    }

                    if (parts.length > 1) {
                        host = parts[1];
                    }
                    if (parts.length > 2) {
                        port = parts[2];
                        iport = Integer.parseInt(port);
                    } else {
                        if (scheme.toLowerCase().equals(HTTPS)) {
                            iport = HTTPSPORT;
                        } else {
                            iport = HTTPPORT;
                        }
                    }
                } else {
                    host = part1;
                    if (parts.length > 1) {
                        port = parts[1];
                        iport = Integer.parseInt(port);
                    } else {
                        if (scheme.toLowerCase().equals(HTTPS)) {
                            iport = HTTPSPORT;
                        } else {
                            iport = HTTPPORT;
                        }
                    }
                }
            }


            if (method.toUpperCase().equals(HTTPGET)) {
                if ((query != null) && (query.length() > 0)) {
                    path += "?" + query;
                }
                if (user.length() > 0) {
                    response = sendGetRequest(scheme, host, iport, path, user, pwd);
                } else {
                    response = sendGetRequest(scheme, host, iport, path);
                }
            }

            if (method.toUpperCase().equals(HTTPPOST)) {
                if (user.length() > 0) {
                    response = sendPostRequest(scheme, host, iport, path, user, pwd);
                } else {
                    response = sendPostRequest(scheme, host, iport, path);
                }

            }

            if (method.toUpperCase().equals(HTTPDELETE)) {
                if (user.length() > 0) {
                    response = sendDeleteRequest(scheme, host, iport, path, user, pwd);
                } else {
                    response = sendDeleteRequest(scheme, host, iport, path);
                }

            }

        } catch (Exception e) {
            throw new Exception("Error during send message", e);
        } finally {
            // reset local variables
            target = null;
            addFile = false;
            addRandomFile = false;
            fileEntity = null;
            headers = null;
            publish = null;
            pubEntity = null;
        }

        return response;
    }

    public void closeSession() {
        try {
            if (response != null) {
                response.close();
                response = null;
            }
        } catch (Exception e) {
            logger.error("Error closing response", e);
        }
    }

    private CloseableHttpResponse sendPostRequest(final String scheme, final String host, final int port, final String path, final String username, final String password) throws Exception {
        try {
            if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
                throw new Exception("For basic authentication username and password are required");
            } else {
                this.username = username;
                this.password = password;
                this.useBasicAuthentication = true;

                target = new HttpHost(host, port, scheme);
                createBasicAuthContext(target);
            }
            response = sendPostRequest(scheme, host, port, path);
        } catch (Exception e) {
            // reset values
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;

            throw new Exception("Error send delete request with authentication", e);
        } finally {
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;
        }
        return response;
    }

    private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path, final String method, final String username, final String password) throws Exception {
        try {
            if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
                throw new Exception("For basic authentication username and password are required");
            } else {
                this.username = username;
                this.password = password;
                this.useBasicAuthentication = true;

                target = new HttpHost(host, port, scheme);
                createBasicAuthContext(target);
            }
            response = sendRequest(scheme, host, port, path, method);
        } catch (Exception e) {
            // reset values
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;

            throw new Exception("Error send delete request with authentication", e);
        } finally {
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;
        }
        return response;
    }

    private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path, final String method) {
        CloseableHttpResponse response = null;
        URI uri = null;
        HttpRequestBase httpRequest = null;

        try {
            uri = getUri(scheme, host, path);

            if (target == null) {
                target = new HttpHost(host, port, scheme);
            }
            httpRequest =  getMessage(uri, method);

            CloseableHttpClient httpclient = getHttpClient(scheme);

            logger.info("Sending request to: {}", httpRequest.toString());

            if (useBasicAuthentication) {
                response = httpclient.execute(target, httpRequest, localContext);
            } else {
                response = httpclient.execute(target, httpRequest);
            }
        } catch (Exception e) {
            logger.error("Error in sending request", e);
        }
        return response;
    }


    private CloseableHttpResponse sendPostRequest(final String scheme, final String host, final int port, final String path) {
        CloseableHttpResponse response = null;
        URI uri = null;
        HttpPost httpPost = null;

        try {
            uri = getUri(scheme, host, path);

            if (target == null) {
                target = new HttpHost(host, port, scheme);
            }
            httpPost = (HttpPost) getMessage(uri, HTTPPOST);

            CloseableHttpClient httpclient = getHttpClient(scheme);

            logger.info("Sending request to: {}", httpPost.toString());

            if (useBasicAuthentication) {
                response = httpclient.execute(target, httpPost, localContext);
            } else {
                response = httpclient.execute(target, httpPost);
            }
        } catch (Exception e) {
            logger.error("Error in sending request", e);
        }
        return response;
    }

    private CloseableHttpResponse sendDeleteRequest(final String scheme, final String host, final int port, final String path, final String username, final String password) throws Exception {
        try {
            if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
                throw new Exception("For basic authentication username and password are required");
            } else {
                this.username = username;
                this.password = password;
                this.useBasicAuthentication = true;

                target = new HttpHost(host, port, scheme);
                createBasicAuthContext(target);
            }

            response = sendDeleteRequest(scheme, host, port, path);

        } catch (Exception e) {
            // reset values
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;

            throw new Exception("Error send delete request with authentication", e);
        } finally {
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;
        }
        return response;
    }

    private CloseableHttpResponse sendDeleteRequest(final String scheme, final String host, final int port, final String path) {
        CloseableHttpResponse response = null;
        URI uri = null;
        HttpDelete httpDelete = null;

        try {
            uri = getUri(scheme, host, path);

            if (target == null) {
                target = new HttpHost(host, port, scheme);
            }
            httpDelete = (HttpDelete) getMessage(uri, HTTPDELETE);

            CloseableHttpClient httpclient = getHttpClient(scheme);

            logger.info("Sending request to: {}", httpDelete.toString());

            if (useBasicAuthentication) {
                response = httpclient.execute(target, httpDelete, localContext);
            } else {
                response = httpclient.execute(target, httpDelete);
            }
        } catch (Exception e) {
            logger.error("Error in sending request", e);
        }
        return response;
    }


    private CloseableHttpResponse sendGetRequest(final String scheme, final String host, final int port, final String path, final String username, final String password) throws Exception {
        try {
            if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
                throw new Exception("For basic authentication username and password are required");
            } else {
                this.username = username;
                this.password = password;
                this.useBasicAuthentication = true;

                target = new HttpHost(host, port, scheme);
                createBasicAuthContext(target);
            }

            response = sendGetRequest(scheme, host, port, path);

        } catch (Exception e) {
            // reset values
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;

            throw new Exception("Error send get request with authentication", e);
        } finally {
            this.username = null;
            this.password = null;
            this.useBasicAuthentication = false;
        }
        return response;
    }

    private CloseableHttpResponse sendGetRequest(final String scheme, final String host, final int port, final String path) {

        URI uri = null;
        HttpGet httpGet = null;

        try {
            uri = getUri(scheme, host, path);

            if (target == null) {
                target = new HttpHost(host, port, scheme);
            }
            httpGet = (HttpGet) getMessage(uri, HTTPGET);

            CloseableHttpClient httpclient = getHttpClient(scheme);

            logger.info("Sending request to: {}", httpGet.toString());

            if (useBasicAuthentication) {
                response = httpclient.execute(target, httpGet, localContext);
            } else {
                response = httpclient.execute(target, httpGet);
            }
        } catch (Exception e) {
            logger.error("Error in sending request", e);
        }

        return response;
    }

    private URI getUri(final String scheme, final String host, final String path) throws Exception {
        URI uri = null;

        try {
            uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .build();
        } catch (Exception e) {
            throw new Exception("Error building uri", e);
        }
        return uri;
    }

    private HttpRequestBase getMessage(final URI uri, final String method) throws Exception {
        HttpRequestBase httpRequest = null;

        if (method.equals(HTTPPOST)) {
            httpRequest = new HttpPost(uri);
        }
        if (method.equals(HTTPDELETE)) {
            httpRequest = new HttpDelete(uri);
        }
        if (method.equals(HTTPGET)) {
            httpRequest = new HttpGet(uri);
        }
        if (httpRequest == null) {
            throw new Exception("Invalid method specified. Expected GET or POST, received: " + method);
        }

        // httpRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        if (headers != null) {
            Set<String> keys = headers.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = headers.get(key);
                logger.info("Adding header {}:, {}", key, value);
                httpRequest.addHeader(key, value);
            }
        }

        RequestConfig dcNoAuth = RequestConfig.custom()
                .setSocketTimeout(socketTimeOut)
                .setConnectTimeout(connectTimeOut)
                .setConnectionRequestTimeout(requestTimeOut)
                .build();

        RequestConfig rc = RequestConfig.copy(dcNoAuth)
                .build();

        httpRequest.setConfig(rc);

        if (addRandomFile) {
            logger.info("Add random file");
            FileBody fileData = getFileEntity();

            HttpEntity reqEntity = null;

            if (publish != null) {
                reqEntity = MultipartEntityBuilder.create()
                        .addPart("dataset", fileData)
                        .addPart("publish", pubEntity)
                        .build();
            } else {
                reqEntity = MultipartEntityBuilder.create()
                        .addPart("dataset", fileData)
                        .build();
            }

            if (httpRequest instanceof HttpPost) {
                ((HttpPost) httpRequest).setEntity(reqEntity);
            }
        } else if (addFile) {
            logger.info("Add previously created file");

            HttpEntity reqEntity = null;

            if (publish != null) {
                reqEntity = MultipartEntityBuilder.create()
                        .addPart("metadata", fileEntity)
                        .addPart("publish", pubEntity)
                        .build();
            } else {
                reqEntity = MultipartEntityBuilder.create()
                        .addPart("metadata", fileEntity)
                        .build();
            }

            if (httpRequest instanceof HttpPost) {
                ((HttpPost) httpRequest).setEntity(reqEntity);
            }
        }

        return httpRequest;
    }

    private void createBasicAuthContext(final HttpHost target) {
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

    /**
     * Get a parameterized http client, based on usage off https and useProxy setting
     * <p/>
     * Assumes a CredentialsProvider credsProvider has been created if basicAuthentication is used!
     *
     * @return a valid CloableHttpClient based on the scheme and useProxy setting
     */
    private CloseableHttpClient getHttpClient(final String scheme) throws Exception {
        CloseableHttpClient httpclient = null;

        try {
            logger.debug("Creating httpclient with schema {} and useProxy {} and useBasicAuth {}", scheme, useProxy, useBasicAuthentication);

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
                if (useBasicAuthentication) {

                    if (useProxy) {
                        httpclient = HttpClients.custom()
                                .setSSLSocketFactory(sslsf)
                                .setProxy(proxy)
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();
                    }
                    if (!useProxy) {
                        httpclient = HttpClients.custom()
                                .setSSLSocketFactory(sslsf)
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();
                    }
                } else {
                    if (useProxy) {
                        httpclient = HttpClients.custom()
                                .setSSLSocketFactory(sslsf)
                                .setProxy(proxy)
                                .build();
                    }


                    if (!useProxy) {
                        httpclient = HttpClients.custom()
                                .setSSLSocketFactory(sslsf)
                                .build();
                    }
                }
            } else {
                // scheme not equal to HTTPS, assumes http!!!!
                if (useBasicAuthentication) {

                    if (useProxy && useBasicAuthentication) {
                        httpclient = HttpClients.custom()
                                .setProxy(proxy)
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();
                    }

                    if (!useProxy && useBasicAuthentication) {
                        httpclient = HttpClients.custom()
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();
                    }
                } else {
                    if (useProxy && !useBasicAuthentication) {
                        httpclient = HttpClients.custom()
                                .setProxy(proxy)
                                .build();
                    }

                    if (!useProxy && !useBasicAuthentication) {
                        httpclient = HttpClients.custom()
                                .build();
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error creating http client", e);
        }

        return httpclient;
    }

    private StringBody getPublishedEntity() {
        pubEntity = new StringBody(getPublish().toString(), ContentType.TEXT_PLAIN);

        return pubEntity;
    }

    /**
     * Add dummy file
     *
     * @return
     */
    private FileBody getFileEntity() {

        FileBody entity = null;
        File genFile = getNewFile();
        entity = new FileBody(genFile);

        return entity;
    }

    private File getNewFile() {
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
     * Add an existing file
     *
     * @param name
     * @return
     */
    public FileBody getFileEntity(final String name) throws Exception {

        File file = new File(name);
        FileBody entity = null;

        // if file doesnt exists, then create it
        if (!file.exists()) {
            throw new Exception("Try to add file, but it cannot be found or doesnot exist");
        }

        entity = new FileBody(new File(name));
        fileEntity = entity;
        addFile = true;

        return entity;
    }


    public boolean isAddRandomFile() {
        return addRandomFile;
    }

    public void setAddRandomFile(boolean addRandomFile) {
        this.addRandomFile = addRandomFile;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public boolean isUseBasicAuthentication() {
        return useBasicAuthentication;
    }

    public void setUseBasicAuthentication(boolean useBasicAuthentication) {
        this.useBasicAuthentication = useBasicAuthentication;
    }

    public boolean isAddFile() {
        return addFile;
    }

    public void setAddFile(boolean addFile) {
        this.addFile = addFile;
    }


    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = new Boolean(publish);
        getPublishedEntity();
    }
}
