package support;

public class KeyLabelPair {

    private String key;
    private String label;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        String result = "[";

        result += "key: " + key + ", " + "label: " + label;
        result += "]";

        return result;
    }
}
