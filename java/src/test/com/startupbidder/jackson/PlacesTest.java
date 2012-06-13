package test.com.startupbidder.jackson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.Test;

import com.startupbidder.vo.ListingPropertyVO;

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
	
	public static void main (String[] args) throws Exception {
		String json1 = "{\"update_address\":" +
			"{\"address_components\":" +
			"[" +
			"	{\"long_name\":\"Zwonowice\",\"short_name\":\"Zwonowice\",\"types\":[\"locality\",\"political\"]}," +
			"	{\"long_name\":\"Lyski\",\"short_name\":\"Lyski\",\"types\":[\"administrative_area_level_3\",\"political\"]}," + 
			"	{\"long_name\":\"rybnicki\",\"short_name\":\"rybnicki\",\"types\":[\"administrative_area_level_2\",\"political\"]}," +
			"	{\"long_name\":\"Śląskie\",\"short_name\":\"Śląskie\",\"types\":[\"administrative_area_level_1\",\"political\"]}," +
			"	{\"long_name\":\"Poland\",\"short_name\":\"PL\",\"types\":[\"country\",\"political\"]}," +
			"	{\"long_name\":\"44-292\",\"short_name\":\"44-292\",\"types\":[\"postal_code\"]}, "+ 
			"	{\"long_name\":\"44\",\"short_name\":\"44\",\"types\":[\"postal_code_prefix\",\"postal_code\"]}" +
			"]," +
			"\"formatted_address\":\"44-292 Zwonowice, Poland\"," +
			"\"geometry\":{" +
			"	\"location\":{\"Ta\":50.1446108,\"Ua\":18.442436300000054}," +
			"	\"viewport\":{" + 
			"		\"aa\":{\"b\":50.1303074,\"e\":50.1589099}," +
			"		\"ba\":{\"b\":18.410421499999984,\"e\":18.47445110000001} " +
			"	}" +
			"}," +
			"\"icon\":\"http://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\"," +
			"\"id\":\"4d11bbf31d0f55eb5bc80f6eb3ee47d8e82ee33c\"," +
			"\"name\":\"Zwonowice\"," +
			"\"reference\":\"CnRhAAAAhXjSnp3aLyreFVId1-hdPqG5iy7lPcszBDln9-5cHs4q9PioGkEMYxMLz_LFhQ2D8LOK3NT4DjZjiZ6KOZOgBYMGor7T2dLEsB8E7n4bh38d7LkHv0347GJykWdzGhEH-UooUBFWML_OWHneg-B0YRIQC5zt_CPwBoIwwMCT6FbL_hoUXHXuHr8Aao7hZTEOIp5rqhWlFAc\"," +
			"\"types\":[\"locality\",\"political\"]," +
			"\"url\":\"http://maps.google.com/maps/place?ftid=0x471146f47cb45b8d:0x437297e0c3592fa8\"," + 
			"\"vicinity\":\"Zwonowice\"," + 
			"\"html_attributions\":[]" +
			"}" +
			"}";
		
		String json2 = "{\"update_address\":" +
				" {\"address_components\":" +
				"[" +
					"{\"long_name\":\"10\",\"short_name\":\"10\",\"types\":[\"street_number\"]}," +
					"{\"long_name\":\"Rue de Sèze\",\"short_name\":\"Rue de Sèze\",\"types\":[\"route\"]}," +
					"{\"long_name\":\"Molière - Edgard Quinet\",\"short_name\":\"Molière - Edgard Quinet\",\"types\":[\"neighborhood\",\"political\"]}," +
					"{\"long_name\":\"6e Arrondissement\",\"short_name\":\"6e Arrondissement\",\"types\":[\"sublocality\",\"political\"]}," +
					"{\"long_name\":\"Lyon\",\"short_name\":\"Lyon\",\"types\":[\"locality\",\"political\"]}," +
					"{\"long_name\":\"Rhône\",\"short_name\":\"69\",\"types\":[\"administrative_area_level_2\",\"political\"]}," +
					"{\"long_name\":\"Rhône-Alpes\",\"short_name\":\"RA\",\"types\":[\"administrative_area_level_1\",\"political\"]}," +
					"{\"long_name\":\"France\",\"short_name\":\"FR\",\"types\":[\"country\",\"political\"]}," +
					"{\"long_name\":\"69006\",\"short_name\":\"69006\",\"types\":[\"postal_code\"]}" +
				"]," +
				"\"formatted_address\":\"10 Rue de Sèze, 69006 Lyon, France\"," +
				"\"geometry\":{" +
					"\"bounds\":{" +
						"\"aa\":{\"b\":45.767912,\"e\":45.767925},\"ba\":{\"b\":4.84405019999997,\"e\":4.844052499999975}" +
					"}," +
					"\"location\":{\"Ta\":45.767912,\"Ua\":4.844052499999975}," +
					"\"location_type\":\"RANGE_INTERPOLATED\"," +
					"\"viewport\":{\"aa\":{\"b\":45.7665695197085,\"e\":45.7692674802915},\"ba\":{\"b\":4.8427023697084906,\"e\":4.845400330291568}}" +
					"}," +
				"\"types\":[\"street_address\"]}" +
			"}";
		
		ObjectMapper mapper = new ObjectMapper();
		String addressString = json2;
		if (!StringUtils.isEmpty(addressString)) {
			JsonNode rootNode = mapper.readValue(addressString, JsonNode.class);
			List<ListingPropertyVO> properties = new ArrayList<ListingPropertyVO>();
			
			if (rootNode.get("update_address") == null) {
				return;
			}
			JsonNode addressComponents = rootNode.get("update_address").get("address_components");
			if (addressComponents != null) {
				Iterator<JsonNode> elements = addressComponents.getElements();
				for (; elements.hasNext(); ) {
					String[] comp = getAddressComponents(elements.next());
					if (comp != null) {
						System.out.println(ToStringBuilder.reflectionToString(comp));
					}
				}
			}
			JsonNode formattedAddress = rootNode.get("update_address").get("formatted_address");
			if (formattedAddress != null) {
				System.out.println(formattedAddress.getValueAsText());
			}
			JsonNode geometry = rootNode.get("update_address").get("geometry");
			if (geometry != null && geometry.get("location") != null) {
				Iterator<JsonNode> locationIt = geometry.get("location").getElements();
				String ta = locationIt.next().getValueAsText();
				String ua = locationIt.next().getValueAsText();
				
				System.out.println("lat: " + ta + ", long:" + ua);
			}
		}
	}
	
	private static String[] getAddressComponents(JsonNode element) {
		//System.out.println("Element: " + ToStringBuilder.reflectionToString(element));
		String[] address = null;
		
		JsonNode types = element.get("types");
		if (types != null && types instanceof ArrayNode) {
			String type1 = types.get(0) != null ? types.get(0).getValueAsText() : null;
			String type2 = types.get(1) != null ? types.get(1).getValueAsText() : null;
			
			if (!StringUtils.equals("political", type2)) {
				return null;
			}
			address = new String[3];
			address[0] = type1;
			if (element.get("short_name") != null) {
				address[1] = element.get("short_name").getValueAsText();
			}
			if (element.get("long_name") != null) {
				address[2] = element.get("long_name").getValueAsText();
			}
		}
		return address;
	}
	
}