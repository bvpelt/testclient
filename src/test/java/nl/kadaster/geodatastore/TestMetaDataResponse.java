package nl.kadaster.geodatastore;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bvpelt on 10/9/15.
 */
public class TestMetaDataResponse {
    // Logger initialization
    private static Logger logger = LoggerFactory.getLogger(TestMetaDataResponse.class);

    private static String testJsonString = "{\"title\":\"TEST METADATA TITLE\"," +
            "\"summary\":\"TEST METADATA SUMMARY this is the summary of the test metadata\"," +
            "\"keywords\":[\"TEST\",\"METADATA\"]," +
            "\"topicCategories\":[\"Gezondheid\",\"Grenzen\"]," +
            "\"location\":\"Apeldoorn\"," +
            "\"lineage\":\"Lineage\"," +
            "\"license\":\"Public Domain\"," +
            "\"resolution\":1000," +
            "\"identifier\":\"5a39a71e-aa28-45af-82ce-05d22ee3c57e\"," +
            "\"url\":\"https://test.geodatastore.pdok.nl:443/geonetwork/id/dataset/5a39a71e-aa28-45af-82ce-05d22ee3c57e/somefile.txt\"," +
            "\"extent\":\"Apeldoorn\"," +
            "\"error\":\"false\"," +
            "\"messages\":[\"message 1\", \"message 2\"]," +
            "\"status\":\"draft\"," +
            "\"fileType\":\"application/octet-stream\"," +
            "\"locationUri\":\"http://geodatastore.pdok.nl/registry/location#Nederland_country\"," +
            "\"thumbnail\":\"thumbnail\"," +
            "\"changeDate\":\"10-11-2015\"," +
            "\"valid\":\"true\"}";

    @Test
    public void TestParseString() {
        String testName = "TestParseString";
        logger.info("Start test {}", testName);

        MetaDataResponse mdr = new MetaDataResponse();

        String title = "TEST METADATA TITLE";
        String summary = "TEST METADATA SUMMARY this is the summary of the test metadata";
        String[] keywords = {"TEST", "METADATA"};
        String[] topicCategories = {"Gezondheid", "Grenzen"};
        String location = "Apeldoorn";
        String lineage = "Lineage";
        String license = "Public Domain";
        int resolution = 1000;
        String changeDate = "10-11-2015";
        String error = "false";
        String extent = "Apeldoorn";
        String filetype = "Application/Json";
        String identifier = "5a39a71e-aa28-45af-82ce-05d22ee3c57e";
        String[] messages = {"message 1", "message 2"};
        String status = "draft";
        String thumbnail = "thumbnail";
        String url = "https://test.geodatastore.pdok.nl:443/geonetwork/id/dataset/5a39a71e-aa28-45af-82ce-05d22ee3c57e/somefile.txt";
        String valid = "true";

        mdr.setChangeDate(changeDate);
        mdr.setError(error);
        mdr.setExtent(extent);
        mdr.setFiletype(filetype);
        mdr.setIdentifier(identifier);
        mdr.setKeywords(keywords);
        mdr.setLicense(license);
        mdr.setLineage(lineage);
        mdr.setLocation(location);
        mdr.setMessages(messages);
        mdr.setResolution(resolution);
        mdr.setStatus(status);
        mdr.setSummary(summary);
        mdr.setThumbnail(thumbnail);
        mdr.setTitle(title);
        mdr.setTopicCategories(topicCategories);
        mdr.setUrl(url);
        mdr.setValid(valid);

        Assert.assertNotNull(mdr.getChangeDate());
        Assert.assertEquals(true, mdr.getChangeDate().equals(changeDate));
        Assert.assertNotNull(mdr.getError());
        Assert.assertEquals(true, mdr.getError().equals(error));
        Assert.assertNotNull(mdr.getExtent());
        Assert.assertEquals(true, mdr.getExtent().equals(extent));
        Assert.assertNotNull(mdr.getFiletype());
        Assert.assertNotNull(mdr.getIdentifier());
        Assert.assertNotNull(mdr.getKeywords());
        Assert.assertEquals(2, mdr.getKeywords().length);
        Assert.assertNotNull(mdr.getLicense());
        Assert.assertNotNull(mdr.getLineage());
        Assert.assertNotNull(mdr.getLocation());
        Assert.assertNotNull(mdr.getMessages());
        Assert.assertNotNull(mdr.getResolution());
        Assert.assertNotNull(mdr.getStatus());
        Assert.assertNotNull(mdr.getSummary());
        Assert.assertNotNull(mdr.getThumbnail());
        Assert.assertNotNull(mdr.getTitle());
        Assert.assertNotNull(mdr.getTopicCategories());
        Assert.assertEquals(2, mdr.getTopicCategories().length);
        Assert.assertNotNull(mdr.getUrl());
        Assert.assertNotNull(mdr.getValid());
    }

    @Test
    public void TestStringToJson() {
        String testName = "TestStringToJson";
        logger.info("Start test {}", testName);
        JsonConverter json = new JsonConverter();

        String changeDate = "10-11-2015";
        String error = "false";
        String extent = "Apeldoorn";

        json.loadString(testJsonString);

        MetaDataResponse mdr = new MetaDataResponse();
        // fill metadata with received metadata

        mdr.setTitle(json.getStringNode("title"));
        mdr.setSummary(json.getStringNode("summary"));
        mdr.setKeywords(json.getStringArray("keywords"));
        mdr.setTopicCategories(json.getStringArray("topicCategories"));
        mdr.setLocation(json.getStringNode("location"));
        mdr.setLineage(json.getStringNode("lineage"));
        mdr.setLicense(json.getStringNode("license"));
        String res = json.getStringNode("resolution");
        if ((null == res) || (res.length()==0) || (res.equals("null"))) {
            res = "0";
        }
        mdr.setResolution(Integer.parseInt(res));
        mdr.setIdentifier(json.getStringNode("identifier"));
        mdr.setUrl(json.getStringNode("url"));
        mdr.setExtent(json.getStringNode("extent"));
        mdr.setError(json.getStringNode("error"));
        mdr.setMessages(json.getStringArray("messages"));
        mdr.setStatus(json.getStringNode("status"));
        mdr.setFiletype(json.getStringNode("fileType"));
        mdr.setLocationUri(json.getStringNode("locationUri"));
        mdr.setThumbnail(json.getStringNode("thumbnail"));
        mdr.setChangeDate(json.getStringNode("changeDate"));
        mdr.setValid(json.getStringNode("valid"));


        Assert.assertNotNull(mdr.getChangeDate());
        Assert.assertEquals(true, mdr.getChangeDate().equals(changeDate));
        Assert.assertNotNull(mdr.getError());
        Assert.assertEquals(true, mdr.getError().equals(error));
        Assert.assertNotNull(mdr.getExtent());
        Assert.assertEquals(true, mdr.getExtent().equals(extent));
        Assert.assertNotNull(mdr.getFiletype());
        Assert.assertNotNull(mdr.getIdentifier());
        Assert.assertNotNull(mdr.getKeywords());
        Assert.assertEquals(2, mdr.getKeywords().length);
        Assert.assertNotNull(mdr.getLicense());
        Assert.assertNotNull(mdr.getLineage());
        Assert.assertNotNull(mdr.getLocation());
        Assert.assertNotNull(mdr.getMessages());
        Assert.assertNotNull(mdr.getResolution());
        Assert.assertNotNull(mdr.getStatus());
        Assert.assertNotNull(mdr.getSummary());
        Assert.assertNotNull(mdr.getThumbnail());
        Assert.assertNotNull(mdr.getTitle());
        Assert.assertNotNull(mdr.getTopicCategories());
        Assert.assertEquals(2, mdr.getTopicCategories().length);
        Assert.assertNotNull(mdr.getUrl());
        Assert.assertNotNull(mdr.getValid());
    }
}