package nl.kadaster.geodatastore;

/**
 * Created by PeltB on 22-1-2016.
 */
public class RegistryItem {
    private int key;
    private String label;
    private String coordWest; // optional
    private String coordEast; // optional
    private String coordSouth; // optional
    private String coordNorth; // optional

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
