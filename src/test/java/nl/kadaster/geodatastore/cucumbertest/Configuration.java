package nl.kadaster.geodatastore.cucumbertest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bvpelt on 10/7/15.
 */
public class Configuration {
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    //private static String host = "http://test1:password@ngr3.geocat.net";
    private static String ngr_scheme = "http";
    private static String ngr_username = "test1";
    private static String ngr_password = "password";
    private static String ngr_host = "ngr3.geocat.net";

    private static String pdok_scheme = "https";
    private static String pdok_username = "WPM";
    private static String pdok_password = "testtest";
    private static String pdok_host = "test.geodatastore.pdok.nl";

    private static String fullurl = null;
    private boolean usePdok = false;

    public Configuration(boolean usePdok) {
        this.usePdok = usePdok;
        if (usePdok) {
            fullurl = pdok_scheme + "://" + pdok_username + ":" + pdok_password + "@" + pdok_host;
        } else {
            fullurl = ngr_scheme + "://" + ngr_username + ":" + ngr_password + "@" + ngr_host;
        }
    }

    private Configuration() {

    }

    public String getFullUrl() {
        return fullurl;
    }

    public String getUserName() {
        if (usePdok) {
            return pdok_username;
        } else {
            return ngr_username;
        }
    }

    public String getPassword() {
        if (usePdok) {
            return pdok_password;
        } else {
            return ngr_password;
        }
    }
}
