
# Usage

To use the testclient:
+ Create a testclient
+ Create and send a request
+ Optionally add post parameters before sending the request

## Create Testclient
When creating the Testclient you can also set the proxy to use for a request

	testclient = new TestClient();
    if (useProxy) {
    	testclient.setProxy("www-proxy.cs.kadaster.nl", 8082);
    }


## Create a Request
In order to create a request
+ create a url with http(s) as scheme and optionally a username/password combination
+ add user headers like for instance an `Accept` header
+ send the request and specify the kind of http request to use HTTPGET|HTTPPOST


    url = "https://user:password@myhost.domain.com/contextroot?parameter=1";
    testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    response = testclient.sendRequest(Url, TestClient.HTTPGET);
       
## Post parameters
To use post parameters, first add all required post parameters add headers and send the request

     File randomFile = getRandomFile();
     testclient.addPostFile("dataset", randomFile);
     
     File thumbnailFile = getThumbnailFile();
     testclient.addPostFile("thumbmail", thumbnailFile);
        
     boolean publish = true;
     testclient.addPostString("publish", Boolean.toString(publish));
     
     testclient.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
     response = testclient.sendRequest(Url, TestClient.HTTPPOST);
 