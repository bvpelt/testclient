package nl.kadaster.geodatastore;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
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

    /**
     * Convert json as a text string to json nodes
     *
     * @param input
     */
    public void loadString(final String input) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            node = mapper.readTree(input);
        } catch (Exception e) {
            logger.error("Error parsing json string {}", input, e);
        }
    }

    /**
     * Get text value of a json field
     *
     * @param fieldName
     * @return null, if not found of text representation of field
     */
    public String getStringNode(final String fieldName) {
        String result = null;
        if (node != null) {
            result = node.get(fieldName).asText();
        }
        return result;
    }

    /**
     * Get text value of a json field
     *
     * @param fieldName
     * @return null, if not found of text representation of field
     */
    public String[] getStringArray(final String fieldName) {
        String[] result = null;
        if (node != null) {
            ArrayNode kw;
            kw = (ArrayNode)node.get("keywords");

            int maxSize = kw.size();

            result = new String[maxSize];

            int i =0;
            while (i < maxSize) {
                result[i] = kw.get(i).getTextValue();
                i++;
            }
        }
        return result;
    }

    /**
     * Get string representation of a MetaData object
     *
     * @param md
     * @return
     * @throws Exception
     */
    public String getObjectJson(final MetaData md) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(md);
        } catch (Exception e) {
            throw new Exception("Error converting meta data to json", e);
        }
        return result;
    }

    /**
     * Get string representation of an Object
     *
     * @param object
     * @return
     * @throws Exception
     */
    public String getObjectJson(final Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new Exception("Error converting object to json", e);
        }
        return result;
    }

    /**
     * Get json node tree of previously loaded and parsed string
     *
     * @return
     */
    public JsonNode getNode() {
        return this.node;
    }
}
