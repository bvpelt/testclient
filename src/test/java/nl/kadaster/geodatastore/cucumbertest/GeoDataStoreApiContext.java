package nl.kadaster.geodatastore.cucumbertest;

import java.util.UUID;

/**
 * Created by bvpelt on 10/10/15.
 */
public class GeoDataStoreApiContext {

    private static GeoDataStoreApiContext instance = null;

    private String dataSetIdentifier;

    private String resultJson;


    private UUID uuid;

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
}
