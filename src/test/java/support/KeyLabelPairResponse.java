package support;

public class KeyLabelPairResponse {

    private KeyLabelPair[] response;

    public KeyLabelPair[] getResponse() {
        return response;
    }

    public void setResponse(KeyLabelPair[] response) {
        this.response = response;
    }

    @Override
    public String toString() {
        String result;

        result = "response: " + "[";
        int len = response.length;
        for (int i = 0; i < len; i++) {
            result += response[i] + ", ";
        }
        result += "]";
        return result;
    }

}
