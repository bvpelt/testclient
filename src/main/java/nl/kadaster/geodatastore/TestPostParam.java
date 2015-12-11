package nl.kadaster.geodatastore;


import org.apache.http.entity.mime.content.ContentBody;

/**
 * Created by bvpelt on 11/21/15.
 */
public class TestPostParam {

    private String name;

    private ContentBody value;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ContentBody getValue() {
        return value;
    }

    public void setValue(final ContentBody value) {
        this.value = value;
    }

}
