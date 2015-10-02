package nl.kadaster.geodatastore;

/**
 * Created by peltb on 10/2/2015.
 */
public class FieldCounter {

    private String name;
    private Boolean conformSpec;
    private Boolean conformMessage;
    private Boolean valueNull;

    public FieldCounter() {
        name = "";
        conformSpec = false;
        conformMessage = false;
    }

    public FieldCounter(final String name) {
        this.name = name;
        conformSpec = false;
        conformMessage = false;
    }

    public FieldCounter(final String name, boolean conformSpec) {
        this.name = name;
        this.conformSpec = conformSpec;
        conformMessage = false;
    }

    public Boolean getConformMessage() {
        return conformMessage;
    }

    public void setConformMessage(Boolean conformMessage) {
        this.conformMessage = conformMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getConformSpec() {
        return conformSpec;
    }

    public void setConformSpec(Boolean conformSpec) {
        this.conformSpec = conformSpec;
    }


    public Boolean getValueNull() {
        return valueNull;
    }

    public void setValueNull(Boolean valueNull) {
        this.valueNull = valueNull;
    }

}
