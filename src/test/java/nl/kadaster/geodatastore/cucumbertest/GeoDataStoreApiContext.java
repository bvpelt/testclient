package nl.kadaster.geodatastore.cucumbertest;

/**
 * Created by bvpelt on 10/10/15.
 */
public class GeoDataStoreApiContext {

    private static GeoDataStoreApiContext instance = null;

    private String dataSetIdentifier;

    private String resultJson;

    protected GeoDataStoreApiContext() {

    }

    public static GeoDataStoreApiContext getInstance() {
        if (null == instance) {
            instance = new GeoDataStoreApiContext();
        }

        return instance;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public String getDataSetIdentifier() {
        return dataSetIdentifier;
    }

    public void setDataSetIdentifier(final String dataSetIdentifier) {
        this.dataSetIdentifier = dataSetIdentifier;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(final String resultJson) {
        this.resultJson = resultJson;
    }
}
