package nl.kadaster.geodatastore;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peltb on 10/1/2015.
 */
public class JsonConverter {
    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    private JsonNode node = null;

    public JsonConverter() {

    }

    public void loadString(final String input) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            node = mapper.readTree(input);

        } catch (Exception e) {
            logger.error("Error parsing json string {}", input, e);
        }
    }
}
