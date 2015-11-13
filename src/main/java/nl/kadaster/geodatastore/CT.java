package nl.kadaster.geodatastore;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.NestableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by bvpelt on 9/29/15.
 */
public class CT {
    private static Logger logger = LoggerFactory.getLogger(CT.class);

    public CT() {
        String configname = "test.properties";
        try {
            Configuration config = new PropertiesConfiguration(configname);

            String scheme = config.getString("scheme");
            String scheme1 = config.getString("scheme1");

            logger.info("found scheme: {}, scheme1: {}", scheme, scheme1);
        } catch (Exception e) {
            logger.error("Couldnot open config file " + configname);
        }
    }

    public static void main(String[] args) {

        try {
            CT ct = new CT();


        } catch (Exception e) {
            logger.error("App received error: ", e);
        }
    }
}
