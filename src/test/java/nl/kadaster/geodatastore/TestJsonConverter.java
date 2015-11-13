package nl.kadaster.geodatastore;

import org.codehaus.jackson.JsonNode;
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
    public void TestParseString() {
        String testName = "TestParseString";
        logger.info("Start test {} input {}", testName, testJsonString);
        JsonConverter json = new JsonConverter();
        json.loadString(testJsonString);

        JsonNode node = json.getNode();

        Iterator<String> fieldNames = node.getFieldNames();
        int numFields = 0;
        while (fieldNames.hasNext()) {
            numFields++;
            String fieldName = fieldNames.next();
            String fieldValue = json.getStringNode(fieldName);
            logger.info("Found field: {} value {}", fieldName, fieldValue);
        }
        Assert.assertEquals(numFields, 19);
        logger.info("End   test {}", testName);
    }

    @Test
    public void TestCheckJson() {
        String testName = "TestCheckJson";
        logger.info("Start test {}", testName);
        JsonConverter json = new JsonConverter();
        json.loadString(testJsonString);

        JsonNode node = json.getNode();
        HashMap<String, FieldCounter> checkList = new HashMap<String, FieldCounter>();
        int jsonFields = 0;
        Iterator<String> jsonFieldNames = node.getFieldNames();
        Iterator<String> fieldNames = knownFields.iterator();

        int numberFields = 0;
        int notNullFields = 0;
        int filledFields = 0;
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            FieldCounter fc = new FieldCounter(fieldName, true);
            String fieldValue = ((node.get(fieldName) == null) ? null : json.getStringNode(fieldName));
            // all fields should be found here!!!!
            // assertNotNull(fieldValue);
            if (fieldValue == null) {
                fc.setValueNull(true);
            }
            if (!checkList.containsKey(fieldName)) {
                checkList.put(fieldName, fc);
            }
            logger.info("Found field: {} value {}", fieldName, fieldValue);
            numberFields++;
            if (fieldValue != null) {
                notNullFields++;
                if (fieldValue.length() > 0) {
                    filledFields++;
                }
            }
        }

        while (jsonFieldNames.hasNext()) {
            jsonFields++;
            String fieldName = jsonFieldNames.next();
            FieldCounter fc = checkList.get(fieldName);
            if (fc != null) {
                fc.setConformMessage(true);
            }
        }

        Set<String> keys = checkList.keySet();
        Iterator<String> key = keys.iterator();
        while (key.hasNext()) {
            FieldCounter fc = null;
            String fieldName = key.next();
            fc = checkList.get(fieldName);
            if (fc.getConformSpec() != fc.getConformMessage()) {
                logger.error("For field {}, the spec is {} in the message is {}", fc.getName(), fc.getConformSpec(), fc.getConformMessage());
            }
        }
        logger.info("Parsed string, numberFields expected {}, jsonFields in string {}, numberfields not null {}, numberfields filled {}", numberFields, jsonFields, notNullFields, filledFields);
        logger.info("End   test {}", testName);

    }


}
