package support;

public class LocationResponse {
    private LocationKeyValuePair[] response;

    public LocationKeyValuePair[] getResponse() {
        return response;
    }

    public void setResponse(LocationKeyValuePair[] response) {
        this.response = response;
    }

    @Override
    public String toString() {
        String result;
        result = "response: [";

        int len = response.length;
        for (int i = 0; i < len; i++) {
            result += response[i] + ", ";
        }
        result += "]";

        return result;
    }
}
