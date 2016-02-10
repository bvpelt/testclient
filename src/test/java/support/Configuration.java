package support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bvpelt on 10/7/15.
 */
public class Configuration {
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    private String scheme = "https";
    private String username = "WPM";
    private String password = "testtest";
    private String host = "test.geodatastore.pdok.nl";
    private boolean useproxy = false;
    private String proxyHost = "www-proxy.cs.kadaster.nl";
    private int proxyPort = 8082;
    private int connectTimeOut = 5000; // ms
    private int requestTimeOut = 5000; // ms
    private int socketTimeOut = 5000; // ms
    
    private String dataset = "dataset";
    private String datasets = "datasets";
    private String registries = "registries";
    private String registry = "registry";
    private String apiversion = "/api/v1/";

  

    public Configuration() {
    	loadConfig();
    }
    
    private void loadConfig() {
    	logger.debug("Start: Loading configuration");
        String configname = "test.properties";
        Properties prop = new Properties();
        InputStream input = null;

        try {
            // input = new FileInputStream(configname);
        	
            input = this.getClass().getClassLoader().getResourceAsStream(configname);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            username = prop.getProperty("username", username);
            logger.debug("Configuration username: {}", username);
            
            password = prop.getProperty("password", password);
            logger.debug("Configuration password: {}", password);
            
            String b = prop.getProperty("useproxy");            
            if (b.equals("true")) {
            	useproxy = true;
            }
            
            if (b.equals("false")) {
            	useproxy = false;
            }
            logger.debug("Configuration useproxy: {}", useproxy);
            
            b = prop.getProperty("scheme");       
            if (b.equals("https") || b.equals("http")) {
            	scheme = b;
            }
            logger.debug("Configuration scheme: {}", scheme);
            
            host = prop.getProperty("host");
            logger.debug("Configuration host: {}", host);

            proxyHost = prop.getProperty("proxyhost", proxyHost);
            logger.debug("Configuration proxyhost: {}", proxyHost);

            proxyPort = Integer.parseInt(prop.getProperty("proxyport", Integer.toString(proxyPort)));
            logger.debug("Configuration proxyport: {}", proxyPort);
            
            connectTimeOut = Integer.parseInt(prop.getProperty("connecttimeOut", Integer.toString(connectTimeOut)));
            logger.debug("Configuration connectTimeOut: {}", connectTimeOut);
            
            requestTimeOut = Integer.parseInt(prop.getProperty("requesttimeOut", Integer.toString(requestTimeOut)));
            logger.debug("Configuration requestTimeOut: {}", requestTimeOut);
            
            socketTimeOut = Integer.parseInt(prop.getProperty("sockettimeOut", Integer.toString(socketTimeOut)));
            logger.debug("Configuration socketTimeOut: {}", socketTimeOut);

        } catch (IOException ex) {
            logger.error("Error loading configuration: {}", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Error closing configuration file: {}", e);
                }
            }
        }
        logger.debug("End  : Loading configuration");
    }

    public String getBaseUrl(final boolean useAuthentication) {
        String fullurl;

        if (useAuthentication) {
            fullurl = scheme + "://" + username + ":" + password + "@" + host;
        } else {
            fullurl = scheme + "://" + host;
        }

        return fullurl;
    }

    public String getDataset(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + apiversion + dataset;

        return fullurl;
    }

    public String getDownloadUrl(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + "/id/" + dataset;

        return fullurl;
    }

    public String getDatasets(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + apiversion + datasets;

        return fullurl;
    }

    public String getRegistries(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + apiversion + registries;

        return fullurl;
    }

    public String getRegistry(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + apiversion + registry;

        return fullurl;
    }

    public String getDelete(final boolean useAuthentication) {
        String fullurl;

        fullurl = getBaseUrl(useAuthentication) + apiversion + dataset;

        return fullurl;
    }

    public boolean isUseproxy() {
        return useproxy;
    }

    public void setUseproxy(boolean useproxy) {
        this.useproxy = useproxy;
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

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
