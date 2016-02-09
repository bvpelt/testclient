package support;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConverter {

    public <T> T toObject(String jsonString, Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {

        T obj = null;

        ObjectMapper mapper = new ObjectMapper();

        obj = mapper.readValue(jsonString, valueType);

        return obj;
    }

    /**
     * Get string representation of an object
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public String toJson(final Object obj) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        String result = null;
        try {
            result = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new Exception("Error converting meta data to json", e);
        }

        return result;
    }

}
