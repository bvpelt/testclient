package support;

public class MetaData {
    private String title;
    private String summary;
    private String[] keywords;
    private String[] topicCategories;

    private String locationUri;
    private String lineage;
    private String license;
    private String resolution;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String[] getTopicCategories() {
        return topicCategories;
    }

    public void setTopicCategories(String[] topicCategories) {
        this.topicCategories = topicCategories;
    }

    public String getLocationUri() {
        return locationUri;
    }

    public void setLocationUri(String locationUri) {
        this.locationUri = locationUri;
    }

    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        String result;

        result = "title: " + title + ", " + "summary: " + summary + ", " + "keywords: " + getKeywordList() + ", "
                + "topicCategories: " + getTopicCategorieList() + ", " + "locationUri: " + locationUri + ", "
                + "lineage: " + lineage + ", " + "license: " + license + ", " + "resolution: " + resolution + ", ";

        return result;
    }

    private String getKeywordList() {
        String kw = "[";
        if (keywords.length > 0) {

            for (String element : keywords) {
                kw += element + ",";
            }
        }
        kw += "]";
        return kw;
    }

    private String getTopicCategorieList() {
        String kw = "[";
        if (topicCategories.length > 0) {
            for (String element : topicCategories) {
                kw += element + ",";
            }
        }
        kw += "]";
        return kw;
    }

}
