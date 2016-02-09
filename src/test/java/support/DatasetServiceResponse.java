package support;

public class DatasetServiceResponse extends MetaData {

    private String identifier;
    private String url;
    private String thumbnailUri;
    private String extent;
    private Boolean error;
    private String[] messages;
    private String status;
    private String fileType;
    private String location;
    private String changeDate;
    private String publishDate;

    private String fileName;
    private Boolean valid;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        String result;

        result = super.toString() + "identifier: " + identifier + ", " + "url: " + url + ", " + "extent: " + extent
                + ", " + "error: " + Boolean.toString(error) + ", " + "messages: " + getMessageList() + ", "
                + "status: " + status + ", " + "fileType: " + fileType + ", " + "location: " + location + ", "
                + "changeDate: " + changeDate + ", " + "publishDate: " + publishDate + ", " + "thumbnailUri: "
                + thumbnailUri + ", " + "fileName: " + fileName + ", " + "valid: " + Boolean.toString(valid);

        return result;
    }

    private String getMessageList() {
        String kw = "[";
        if (messages.length > 0) {
            for (String element : messages) {
                kw += element + ",";
            }
        }
        kw += "]";
        return kw;
    }
}
