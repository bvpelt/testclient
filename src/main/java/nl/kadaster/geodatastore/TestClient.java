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
import org.apache.http.impl.client.*;
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
public class TestClient {
    // Public accessable constants
    public static String HTTPS = "https";
    public static String HTTPGET = "GET";
    public static String HTTPPOST = "POST";
    public static String HTTPDELETE = "DELETE";

    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(TestClient.class);
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
    private boolean addThumbnail = false;
    private boolean addMetaData = false;
    private FileBody fileEntity;

    // option publish
    private Boolean publish = null;
    private StringBody pubEntity;
    private StringBody metaDataEntity;
    private String metaData = null;

    // The keystore password (used for TLS connections and proxy)
    private String keystorepwd = "geodatastore";

    // http request parameters in seconds
    private int socketTimeOut = 5000;
    private int connectTimeOut = 5000;
    private int requestTimeOut = 5000;

    private HttpHost target = null;
    private CloseableHttpResponse response = null;

    public TestClient() {
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
        addThumbnail = false;

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

            //some url have pattern scheme://host:port/path
            // remove port if it is 443 so that ssl has no problems verifying hostname
            String [] hostparts = host.split(":");
            if (hostparts.length > 1) {
                if (hostparts[1].equals("443")) {
                    host = hostparts[0];
                }
            }

            //if (method.toUpperCase().equals(HTTPGET)) {
                if ((query != null) && (query.length() > 0)) {
                    path += "?" + query;
                }
            //}

            if (user.length() > 0) {
                response = sendRequest(scheme, host, iport, path, method, user, pwd);
            } else {
                response = sendRequest(scheme, host, iport, path, method);
            }
        } catch (Exception e) {
            throw new Exception("Error during send message", e);
        } finally {
            // reset local variables
            target = null;
            addFile = false;
            addRandomFile = false;
            addThumbnail = false;
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

    private void createBasicAuthContext(final HttpHost target) {
        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(target.getHostName(),
                target.getPort()),
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

    private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path, final String method, final String username, final String password) throws Exception {
        try {
            if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
                throw new Exception("For basic authentication username and password are required");
            } else {
                logger.info("Using basic authentication for user: {}", username);
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

    private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path, final String method) {
        CloseableHttpResponse response = null;
        URI uri = null;
        HttpRequestBase httpRequest = null;

        try {
            uri = getUri(scheme, host, path);

            if (target == null) {
                target = new HttpHost(host, port, scheme);
            }
            httpRequest = getMessage(uri, method);

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
            throw new Exception("Unknown method specified. Expected GET, POST or DELETE received: " + method);
        }

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

        HttpEntity reqEntity = null;
        MultipartEntityBuilder mb=MultipartEntityBuilder.create();

        if (publish != null) {
            mb.addPart("publish", pubEntity);
        }
        if (addRandomFile) {
            logger.info("Add random file");
            FileBody fileData = getFileEntity();
            mb.addPart("dataset", fileData);
        }
        if (addMetaData) {
            logger.info("Add metadata");

            getMetaDataEntity();
            mb.addPart("metadata", metaDataEntity);
        }

        if (addThumbnail) {
            logger.info("Add thumbnail file");
            FileBody thumbnailData = getThumbnail();
            mb.addPart("thumbnail", thumbnailData);
        }

        reqEntity = mb.build();

        if (httpRequest instanceof HttpPost) {
            ((HttpPost) httpRequest).setEntity(reqEntity);
        }

        return httpRequest;
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

            boolean isSecure = false;
            SSLConnectionSocketFactory sslsf = null;

            if (scheme.equals(HTTPS)) {
                isSecure = true;
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(new File("proxykeystore.jks"), keystorepwd.toCharArray(),
                                new TrustSelfSignedStrategy())
                        .build();

                // Allow TLSv1 protocol only
                sslsf = new SSLConnectionSocketFactory(
                        sslcontext,
                        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            }

            // Alternative start
            HttpClientBuilder clientBuilder = HttpClients.custom();

            if (isSecure) {
                clientBuilder = clientBuilder.setSSLSocketFactory(sslsf);
            }

            if (useBasicAuthentication) {
                clientBuilder = clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            if (useProxy) {
                clientBuilder = clientBuilder.setProxy(proxy);
            }

            httpclient = clientBuilder.build();
            // Alternative end

        } catch (Exception e) {
            logger.error("Error creating http client", e);
        }

        return httpclient;
    }

    private StringBody getPublishedEntity() {
        pubEntity = new StringBody(getPublish().toString(), ContentType.TEXT_PLAIN);

        return pubEntity;
    }

    private StringBody getMetaDataEntity() {
        metaDataEntity = new StringBody(getMetaData(), ContentType.APPLICATION_JSON);

        return metaDataEntity;
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

    private FileBody getThumbnail() {
        FileBody entity = null;
        File genFile = getThumbnailFile();
        entity = new FileBody(genFile);

        return entity;
    }

    private File getThumbnailFile() {
        String thumbnailFileName = "xls.png";
        File file = null;

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            file = new File(classLoader.getResource(thumbnailFileName).getFile());
        } catch (Exception e) {
            logger.error("Couldnot find file: {}", thumbnailFileName, e);
        }
        return file;
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

    public void setAddRandomFile(final boolean addRandomFile) {
        this.addRandomFile = addRandomFile;
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

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public boolean isUseBasicAuthentication() {
        return useBasicAuthentication;
    }

    public void setUseBasicAuthentication(final boolean useBasicAuthentication) {
        this.useBasicAuthentication = useBasicAuthentication;
    }

    public boolean isAddFile() {
        return addFile;
    }

    public void setAddFile(final boolean addFile) {
        this.addFile = addFile;
    }

    public boolean isAddThumbnail() {
        return addThumbnail;
    }

    public void setAddThumbnail(final boolean addThumbnail) {
        this.addThumbnail = addThumbnail;
    }


    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(final Boolean publish) {
        this.publish = new Boolean(publish);
        getPublishedEntity();
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(final String metaData) {
        this.metaData = metaData;
        addMetaData = true;
    }

    public boolean isAddMetaData() {
        return addMetaData;
    }

    public void setAddMetaData(final boolean addMetaData) {
        this.addMetaData = addMetaData;
    }
}
