package nl.kadaster.geodatastore;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by peltb on 10/1/2015.
 */
public class TestJsonConverter {
    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(TestJsonConverter.class);


    @Test
    public void TestParseString() {
        String s = "{\"title\":\"\",\"summary\":\"\",\"keywords\":[],\"topicCategories\":[],\"location\":null,\"lineage\":\"\",\"useLimitation\":null,\"license\":\"http://creativecommons.org/licenses/by/4.0/\",\"resolution\":null,\"identifier\":\"6a8672f6-0733-487c-a062-ab4e6aef154b\",\"url\":\"http://10.103.55.29:8080/geonetwork/id/dataset/6a8672f6-0733-487c-a062-ab4e6aef154b/somefile.txt\",\"extent\":null,\"error\":false,\"messages\":[],\"status\":\"draft\",\"fileType\":\"application/octet-stream\",\"locationUri\":null,\"changeDate\":null,\"valid\":false}";

        JsonConverter json = new JsonConverter();


        json.loadString(s);

        JsonNode node = json.getNode();

        Iterator<String> fieldNames = node.getFieldNames();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            String fieldValue = node.get(fieldName).asText();
            logger.info("Found field: {} value {}", fieldName, fieldValue);
        }
    }

}
