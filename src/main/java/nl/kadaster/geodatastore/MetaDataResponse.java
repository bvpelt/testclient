package nl.kadaster.geodatastore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeltB on 9-10-2015.
 */
public class MetaDataResponse {

    private String title;
    private String summary;
    private List<String> keywordList = new ArrayList<String>();
    private String[] keywords;
    private List<String> topicCategoriesList = new ArrayList<String>();
    private String[] topicCategories;
    private String location;
    private String lineage;
    private String license;
    private int resolution;
    private String identifier;
    private String url;
    private String extent;
    private String error;
    private List<String> messageList = new ArrayList<String>();
    private String[] messages;
    private String status;
    private String filetype;
    private String locationUri;
    private String thumbnail;
    private String changeDate;
    private String valid;

    public MetaDataResponse() {
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(final String extent) {
        this.extent = extent;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String[] getMessages() {
        messages = new String[messageList.size()];
        int i = 0;
        for (String msg: messageList) {
            messages[i++] = msg;
        }
        return messages;
    }

    public void setMessages(final String[] messages) {
        this.messages = messages;
        messageList = new ArrayList<String>();
        for (String msg: messages) {
            messageList.add(msg);
        }
    }

    public void addMessage(final String message) {
        this.messageList.add(message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(final String valid) {
        this.valid = valid;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(final String filetype) {
        this.filetype = filetype;
    }

    public String getLocationUri() {
        return locationUri;
    }

    public void setLocationUri(final String locationUri) {
        this.locationUri = locationUri;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(final String changeDate) {
        this.changeDate = changeDate;
    }

}
