package nl.kadaster.geodatastore;


import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by peltb on 10/1/2015.
 */
public class TestJsonConverter {
    // Logger initialization
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TestJsonConverter.class);

    // Test string
    private static String testJsonString = "{\"title\":\"\",\"summary\":\"\",\"keywords\":[],\"topicCategories\":[],\"location\":null,\"lineage\":\"\",\"useLimitation\":null,\"license\":\"http://creativecommons.org/licenses/by/4.0/\",\"resolution\":null,\"identifier\":\"6a8672f6-0733-487c-a062-ab4e6aef154b\",\"url\":\"http://10.103.55.29:8080/geonetwork/id/dataset/6a8672f6-0733-487c-a062-ab4e6aef154b/somefile.txt\",\"extent\":null,\"error\":false,\"messages\":[],\"status\":\"draft\",\"fileType\":\"application/octet-stream\",\"locationUri\":null,\"changeDate\":null,\"valid\":false}";

    private List<String> knownFields = null;

    @Before
    public void filleKnownFields() {
        knownFields = new ArrayList<String>();
        knownFields.add("title");
        knownFields.add("summary");
        knownFields.add("keywords");
        knownFields.add("topicCategories");
        knownFields.add("location");
        knownFields.add("lineage");
        knownFields.add("license");
        knownFields.add("resolution");
        knownFields.add("identifier");
        knownFields.add("url");
        knownFields.add("thumbnail");
        knownFields.add("extent");
        knownFields.add("error");
        knownFields.add("messages");
        knownFields.add("status");
        knownFields.add("valid");
        knownFields.add("filetype");
        knownFields.add("changeDate");
    }

    @Test
    /**
     * Test jsonstring -> object in object alle velden gevuld.
      */
    public void TestParseString() {
        /*
        String testName = "TestParseString";
        logger.info("Start test {} input {}", testName, testJsonString);
        JsonConverter json = new JsonConverter();
        json.loadString(testJsonString);

        JsonNode node = json.getNode();
        node.elements();

        Iterator<JsonNode> fields = node.elements();
        int numFields = 0;
        while (fields.hasNext()) {
            numFields++;
            String fieldName = fields.next().asText();
            String fieldValue = json.getStringNode(fieldName);
            logger.info("Found field: {} value {}", fieldName, fieldValue);
        }
        Assert.assertEquals(numFields, 19);
        logger.info("End   test {}", testName);
        */
    }




}
