package support;

public class DatasetQueryResponse {
    private int from;
    private int to;
    private int selected;
    private int count;
    private DatasetServiceResponse[] metadata;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DatasetServiceResponse[] getMetadata() {
        return metadata;
    }

    public void setMetadata(DatasetServiceResponse[] metadata) {
        this.metadata = metadata;
    }

    public String toString() {
        String result = "[";
        result += "from: " + from + ", ";
        result += "to: " + to + ", ";
        result += "selected: " + selected + ", ";
        result += "count: " + count + "[";

        int len = metadata.length;
        for (int i = 0; i < len; i++) {
            result += "[ " + metadata[i].toString() + "], ";
        }
        result += "]";
        result += "]";
        return result;
    }

}
