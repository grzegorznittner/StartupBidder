package test.com.startupbidder.jackson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

public class PlacesTest {
	private static final Logger log = Logger.getLogger(PlacesTest.class.getCanonicalName());

	private static String callPlacesSearch(String location, String radius, String name) throws ClientProtocolException, IOException, URISyntaxException {
		HttpClient httpclient = new DefaultHttpClient();
        try {
        	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        	qparams.add(new BasicNameValuePair("key", "AIzaSyCo89U7uOuczSTTksoymWkb9dXuxQZJQCw"));
        	qparams.add(new BasicNameValuePair("location", location));
        	qparams.add(new BasicNameValuePair("radius", radius));
        	qparams.add(new BasicNameValuePair("sensor", "false"));
        	qparams.add(new BasicNameValuePair("name", name));
        	qparams.add(new BasicNameValuePair("language", "pl"));
        	URI uri = URIUtils.createURI("https", "maps.googleapis.com", -1, "/maps/api/place/search/xml", 
        	    URLEncodedUtils.format(qparams, "UTF-8"), null);
        	
        	HttpGet httpget = new HttpGet(uri);

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
	}
	
	private static String callAddPlace(String lattitude, String longitude, String accuracy, String name, String type) throws ClientProtocolException, IOException, URISyntaxException {
		HttpClient httpclient = new DefaultHttpClient();
        try {
        	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        	qparams.add(new BasicNameValuePair("key", "AIzaSyCo89U7uOuczSTTksoymWkb9dXuxQZJQCw"));
        	qparams.add(new BasicNameValuePair("sensor", "false"));
        	
        	URI uri = URIUtils.createURI("https", "maps.googleapis.com", -1, "/maps/api/place/add/json", 
        		URLEncodedUtils.format(qparams, "UTF-8"), null);
        	
        	HttpPost httppost = new HttpPost(uri);
        	httppost.setEntity(new StringEntity("{\"location\": {\"lat\": " + lattitude + ",\"lng\": " + longitude + " }, \"accuracy\": " + accuracy + ","
        			 + "\"name\": \"" + name + "\", \"types\": [\"" + type + "\"], \"language\": \"pl\"}"));

            System.out.println("executing request " + httppost.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httppost, responseHandler);
            System.out.println("response: " + responseBody);
            return responseBody;
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
	}
	
	@Test
	public void test() {
		try {
			String places = callPlacesSearch("-33.8670522,151.1957362", "1000", "startupbidder");
			System.out.println(places);

			//callAddPlace("-40.8670525", "179.1957380", "50", "startupbidder 3", "bar");
			//callAddPlace("-40.8670515", "`", "50", "startupbidder 4", "bar");
			
			System.out.println("****************AFTER ADDING PLACES ********************");
			places = callPlacesSearch("-33.8670522,151.1957362", "1000", "startupbidder");
			System.out.println(places);
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while searching places", e);
		}
	}

}
