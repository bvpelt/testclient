package nl.kadaster.geodatastore;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.config.Registry;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PeltB on 14-1-2016.
 */
public class TestRegistryServicesResponse {
    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(TestRegistryServicesResponse.class);

    String name1 = "demoninator";
    String url1 = "https://test.geodatastore.pdok.nl/api/v1/registry/denominator";
    String name2 = "license";
    String url2 = "https://test.geodatastore.pdok.nl/api/v1/registry/license";
    String name3 = "topic";
    String url3 = "https://test.geodatastore.pdok.nl/api/v1/registry/topic";

    private String testJsonString = "{\"registryServices\":" +
            "[" +
            "{\"name\":\"" + name1 + "\"," +
            "\"url\":\"" + url1 + "\"}," +
            "{\"name\":\"" + name2 + "\"," +
            "\"url\":\"" + url2 + "\"}," +
            "{\"name\":\"" + name3 + "\"," +
            "\"url\":\"" + url3 + "\"}" +
            "]" +
            "}";
    private String testJsonString01 = "{\"registryServices\":[" +
            "{" +
            "\"name\": \"" + name1 + "\"," +
            "\"url\": \"" + url1 + "\"" +
            "}," +
            "{" +
            "\"name\": \"" + name2 + "\"," +
            "\"url\": \"" + url2 + "\"" +
            "}," +
            "{" +
            "\"name\": \"" + name3 + "\"," +
            "\"url\": \"" + url3 + "\"" +
            "}" +
            "]";

    private String tj = "{\"registryServices\":[{\"name\":\"demoninator\",\"url\":\"https://test.geodatastore.pdok.nl/api/v1/registry/denominator\"},{\"name\":\"license\",\"url\":\"https://test.geodatastore.pdok.nl/api/v1/registry/license\"},{\"name\":\"topic\",\"url\":\"https://test.geodatastore.pdok.nl/api/v1/registry/topic\"}]}";

    @Test
    public void TestParseString() {
        String testName = "TestParseString";
        logger.info("Start test {}", testName);

        RegistryService rs1 = new RegistryService();
        rs1.setName(name1);
        rs1.setUrl(url1);

        RegistryService rs2 = new RegistryService();
        rs2.setName(name2);
        rs2.setUrl(url2);

        RegistryService rs3 = new RegistryService();
        rs3.setName(name3);
        rs3.setUrl(url3);

        RegistryService[] regservices = {rs1, rs2, rs3};

        RegistryServicesResponse rsr = new RegistryServicesResponse();
        rsr.setRegistries(regservices);



        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(rsr);
            logger.debug("Converted registryservicesresponse to string: {}", result);
        } catch (Exception e1) {
            logger.error("Error converting object to json", e1);
        }

    }

    @Test
    public void TestLoadString() {
        String testName = "TestLoadString";
        logger.info("Start test {}", testName);
        logger.info("Converting string {} to object", tj);

        ObjectMapper mapper = new ObjectMapper();

        try {
            RegistryServicesResponse rsr = mapper.readValue(tj, RegistryServicesResponse.class);
        } catch (IOException e) {
            logger.error("Conversion error for string: {}",tj);
        }

    }
}
