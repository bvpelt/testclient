package support;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
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
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by bvpelt on 11/21/15.
 */

/**
 * Created by bvpelt on 9/26/15.
 */
public class TestClient {
	// Public accessable constants
	public static String HTTPS = "https";
	public static String HTTPCOPY = "COPY";
	public static String HTTPDELETE = "DELETE";
	public static String HTTPGET = "GET";
	public static String HTTPHEAD = "HEAD";
	public static String HTTPPOST = "POST";
	public static String HTTPPUT = "PUT";

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
	private ArrayList<TestPostParam> postParams = null;

	// Private contexts for basic authentication
	private CredentialsProvider credentialsProvider = null;
	private HttpClientContext localContext = null;

	// optional proxy settings
	private boolean useProxy = false;
	private String proxyHost;
	private int proxyPort;

	// The keystore password (used for TLS connections and proxy)
	private String keystorepwd = "geodatastore";

	// http request parameters in milli seconds
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
	 * @param proxyHost
	 *            the hostname of the proxy
	 * @param proxyPort
	 *            the portnumber of the proxy
	 * @throws Exception
	 *             if either hostname or proxy is not specified proxy can't be
	 *             specified
	 */
	public void setProxy(final String proxyHost, final int proxyPort) throws Exception {
		if ((proxyHost == null) || (proxyHost.length() == 0) || (proxyPort == 0)) {
			throw new Exception("Adding proxy requires a proxy hostname and a proxy port number");
		}
		logger.debug("Setup proxy {}:{}", proxyHost, proxyPort);
		proxy = new HttpHost(proxyHost, proxyPort);
		useProxy = true;
	}

	public void addHeader(final String key, final String value) {
		logger.debug("Add header key: {} with value: {}", key, value);

		if (null == headers) {
			headers = new HashMap<String, String>();
		}
		String curValue = headers.get(key);

		if ((curValue != null) && (curValue.length() > 0)) {
			Object[] o = { key, curValue, value };
			logger.error("Header with name: {} - value: {}, already found new value: {}", o);
		}

		headers.put(key, value);
	}

	/**
	 * Send the specified http request
	 *
	 * @param url
	 *            the requested url
	 * @param method
	 *            the request http method (GET|POST)
	 * @return a response
	 * @throws Exception
	 *             if anything failes
	 */
	public CloseableHttpResponse sendRequest(final String url, final String method) throws Exception {
		logger.debug("send request to url: {} with method: {}", url, method);

		response = null;
		target = null;

		try {
			if (!((method.toUpperCase().equals(HTTPGET))
					|| (method.toUpperCase().equals(HTTPPOST) || (method.toUpperCase().equals(HTTPDELETE))))) {
				throw new Exception("Unknown and unsupported method in url");
			}
			URI uri = URI.create(url);

			String scheme = uri.getScheme();
			String host = uri.getHost();
			String path = uri.getPath();
			String authority = uri.getAuthority();
			String query = uri.getQuery();
			String fragment = uri.getFragment(); // points to location in document http://host/page#fragment

			// if authority
			// split userinfo:host:port,
			// then split userinfo into username password
			String user = "";
			String pwd = "";
			String port = "";
			int iport = 0;

			//
			// Generic definition of authority user:password@host:port
			// determine if there is a "@" in the authority
			if ((authority != null) && (authority.length() > 0)) {
				String[] parts = authority.split("@");
				String part1;

				part1 = parts[0];
				if (authority.contains("@")) { // username password specified
					// (optional)
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
					String[] names = part1.split(":");
					// host = names[0];
					if (names.length > 1) {
						port = names[1];
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

			if ((query != null) && (query.length() > 0)) {
				path += "?" + query;
			}

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
			headers = null;
			postParams = null;
		}

		return response;
	}

	public void closeSession() {
		logger.debug("Close the session");

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
		logger.debug("Create basicauthcontext for host: {} port: {}", target.getHostName(), target.getPort());

		credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
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

	private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path,
			final String method, final String username, final String password) throws Exception {
		logger.debug(
				"Send request with authentication scheme: {}, host: {}, port: {}, path: {}, method: {}, username: {}, password: {}",
				scheme, host, port, path, method, username, password);

		try {
			if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
				throw new Exception("For basic authentication username and password are required");
			} else {
				logger.debug("Using basic authentication for user: {}", username);
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
		logger.debug("Get uri for scheme: {}, host: {}, path: {}", scheme, host, path);

		URI uri = null;
		
		String fullUrl = scheme + "://" + host + path;
		
		uri = new URI(fullUrl);
		
		return uri;
	}

	private CloseableHttpResponse sendRequest(final String scheme, final String host, final int port, final String path,
			final String method) {
		logger.debug("Send request for scheme: {}, host: {}, port: {}, path: {}, method: {}", scheme, host, port, path,
				method);

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

			logger.debug("Sending request to: {}", httpRequest.toString());

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
		logger.debug("getMessage for uri: {}, method: {}", uri.toString(), method);

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
				logger.debug("Adding header {}:, {}", key, value);
				httpRequest.addHeader(key, value);
			}
		}

		RequestConfig dcNoAuth = RequestConfig.custom().setSocketTimeout(socketTimeOut)
				.setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut).build();

		RequestConfig rc = RequestConfig.copy(dcNoAuth).build();

		httpRequest.setConfig(rc);

		HttpEntity reqEntity = null;
		if (postParams != null) {

			MultipartEntityBuilder mb = MultipartEntityBuilder.create();

			for (TestPostParam p : postParams) {
				mb.addPart(p.getName(), p.getValue());
			}
			reqEntity = mb.build();

			if (httpRequest instanceof HttpPost) {
				((HttpPost) httpRequest).setEntity(reqEntity);
			}
		}

		return httpRequest;
	}

	/**
	 * Get a parameterized http client, based on usage off https and useProxy
	 * setting
	 * <p/>
	 * Assumes a CredentialsProvider credsProvider has been created if
	 * basicAuthentication is used!
	 *
	 * @return a valid CloableHttpClient based on the scheme and useProxy
	 *         setting
	 */
	private CloseableHttpClient getHttpClient(final String scheme) throws Exception {
		logger.debug("getHttpClient for scheme: {}", scheme);

		CloseableHttpClient httpclient = null;

		try {
			Object[] o = { scheme, useProxy, useBasicAuthentication };
			logger.debug("Creating httpclient with schema {} and useProxy {} and useBasicAuth {}", o);

			boolean isSecure = false;
			SSLConnectionSocketFactory sslsf = null;

			if (scheme.equals(HTTPS)) {
				isSecure = true;
				// Trust own CA and all self-signed certs
				SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File("proxykeystore.jks"),
						keystorepwd.toCharArray(), new TrustSelfSignedStrategy()).build();

				// Allow TLSv1 protocol only
				sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
						SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			}

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

		} catch (Exception e) {
			logger.error("Error creating http client", e);
		}

		return httpclient;
	}

	private void addPostParam(final TestPostParam param) {
		logger.debug("addPostParam name: {} value: {}", param.getName(), param.getValue());

		if (null == postParams) {
			postParams = new ArrayList<TestPostParam>();
		}
		postParams.add(param);
	}

	public void addPostString(final String name, final String value) {
		logger.debug("addPostString name: {}, value: {}", name, value);

		addPostString(name, value, ContentType.TEXT_PLAIN);
	}

	public void addPostString(final String name, final String value, final ContentType contentType) {
		TestPostParam pp = new TestPostParam();

		pp.setName(name);
		StringBody sb = new StringBody(value, contentType);
		pp.setValue(sb);

		addPostParam(pp);
	}

	public void addPostFile(final String name, final File file) {
		TestPostParam pp = new TestPostParam();

		pp.setName(name);
		FileBody fb = new FileBody(file);
		pp.setValue(fb);

		addPostParam(pp);
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

	/**
	 * Return the statuscode of previously executed sendmessage. If there is no
	 * result return -1
	 *
	 * @return HTTP Status code from previous call or -1 if there is no response
	 */
	public int getStatusCode() {
		int statusCode = -1;

		if (null != response) {
			statusCode = response.getStatusLine().getStatusCode();
		}
		return statusCode;
	}

	/**
	 * Return the resulttext of previously executed sendmessage If there is no
	 * result return null
	 *
	 * @return Result text from previous call or null if there is no response
	 * @throws Exception
	 */
	public String getResultText() throws Exception {
		String resultText = null;
		HttpEntity entity = null;
		if (null != response) {
			try {
				entity = response.getEntity();
				Assert.assertNotNull(entity);
				if (entity != null) {
					String content = EntityUtils.toString(entity);
					Assert.assertNotNull(content);
					StringBuffer resultTextBuffer = new StringBuffer(content);
					Assert.assertNotNull(resultTextBuffer);
					Assert.assertNotEquals(0, resultTextBuffer.toString().length());
					if (resultTextBuffer.toString().length() > 0) {
						resultText = resultTextBuffer.toString();
					}
					logger.debug("Result size: {}, content: {}", content.length(), content);
				}
			} catch (Exception e) {
				throw new Exception("Error extracting result");
			}
		}
		return resultText;
	}

	/**
	 * Return the resulttext of previously executed sendmessage If there is no
	 * result return null
	 *
	 * @return Result text from previous call or null if there is no response
	 * @throws Exception
	 */
	public String getContentDispositionFilename() throws Exception {
		String resultText = "";

		if (null != response) {
			try {
				// "Content-Disposition: attachment; filename="somefile.txt"
				Header header = response.getFirstHeader("Content-Disposition");
				Assert.assertNotNull(header);
				String headerString = header.getValue();
				logger.debug("Header string: {}", headerString);

				String[] parts = headerString.split(";");
				if (parts.length > 1) {
					String fn = parts[1];
					int len = fn.length();
					int i = 0;
					boolean found = (fn.charAt(i) == '"');

					while (!found && (i < len)) {
						found = (fn.charAt(i++) == '"');
					}

					if (found && (i < len)) {
						found = false;
						while (!found && (i < len)) {
							char c = fn.charAt(i);
							if (c != '"') {
								resultText += c;
							} else {
								found = true;
							}
							i++;
						}
					}
				}
				logger.debug("Found filename: {}", resultText);

			} catch (Exception e) {
				throw new Exception("Error extracting result");
			}
		}
		return resultText;
	}
}
