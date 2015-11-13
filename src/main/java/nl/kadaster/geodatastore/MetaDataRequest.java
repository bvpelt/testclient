package nl.kadaster.geodatastore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeltB on 9-10-2015.
 */
public class MetaDataRequest {
    private String title;
    private String summary;
    private List<String> keywordList = new ArrayList<String>();
    private String[] keywords = null;
    private List<String> topicCategoriesList = new ArrayList<String>();
    private String[] topicCategories = null;
  //  private String location;
    private String locationUri;
    private String lineage;
    private String license;
    private int resolution;


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String[] getKeywords() {
        keywords = new String[keywordList.size()];
        int i = 0;
        for (String kw : keywordList) {
            keywords[i++] = kw;
        }
        return keywords;
    }

    public void setKeywords(final String[] keywords) {
        this.keywords = keywords;
        keywordList = new ArrayList<String>();
        for (String kw: keywords) {
            keywordList.add(kw);
        }
    }

    public void addKeyword(final String keyword) {
        this.keywordList.add(keyword);
    }

    public String[] getTopicCategories() {
        topicCategories = new String[topicCategoriesList.size()];
        int i = 0;
        for (String top: topicCategoriesList) {
            topicCategories[i++] = top;
        }
        return topicCategories;
    }

    public void setTopicCategories(final String[] topicCategories) {
        this.topicCategories = topicCategories;
        topicCategoriesList = new ArrayList<String>();
        for (String top: topicCategories) {
            topicCategoriesList.add(top);
        }
    }

    public void addTopicCategorie(final String topicCategorie) {
        this.topicCategoriesList.add(topicCategorie);
    }

    /*
    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }
*/
    public String getLocationUri() {
        return locationUri;
    }

    public void setLocationUri(final String locationUri) {
        this.locationUri = locationUri;
    }

    public String getLineage() {
        return lineage;
    }

    public void setLineage(final String lineage) {
        this.lineage = lineage;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(final String license) {
        this.license = license;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(final int resolution) {
        this.resolution = resolution;
    }
}
