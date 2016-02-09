package support;

public class LocationKeyValuePair extends KeyLabelPair {

    private String coordEast;
    private String coordNorth;
    private String coordSouth;
    private String coordWest;

    public String getCoordEast() {
        return coordEast;
    }

    public void setCoordEast(String coordEast) {
        this.coordEast = coordEast;
    }

    public String getCoordNorth() {
        return coordNorth;
    }

    public void setCoordNorth(String coordNorth) {
        this.coordNorth = coordNorth;
    }

    public String getCoordSouth() {
        return coordSouth;
    }

    public void setCoordSouth(String coordSouth) {
        this.coordSouth = coordSouth;
    }

    public String getCoordWest() {
        return coordWest;
    }

    public void setCoordWest(String coordWest) {
        this.coordWest = coordWest;
    }

    @Override
    public String toString() {
        String result;

        result = super.toString() + "coordEast: " + coordEast + ", " + "coordNorth: " + coordNorth + ", "
                + "coordSouth: " + coordSouth + ", " + "coordWest: " + coordWest + ", ";

        return result;
    }

}
