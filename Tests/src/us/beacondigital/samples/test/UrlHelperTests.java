package us.beacondigital.samples.test;

import us.beacondigital.utils.net.UrlHelper;
import android.test.AndroidTestCase;

public class UrlHelperTests extends AndroidTestCase {
	
	public void testAddParamMethod() {
		
		String baseUrl = "http://beacondigital.us";
		String inputUrl, expected, actual;
		
		inputUrl = baseUrl;
		expected = inputUrl + "?key=value";
		actual = UrlHelper.addParam(inputUrl, "key", "value");
		assertEquals("First params should be added with preceding question mark", expected, actual);

		inputUrl = baseUrl + "?key=value";
		expected = inputUrl + "&key2=value2";
		actual = UrlHelper.addParam(inputUrl, "key2", "value2");
		assertEquals("Subsequent params should be added with preceding ampersand", expected, actual);
		
		inputUrl = baseUrl;
		expected = inputUrl + "?key%20with%20spaces=value";
		actual = UrlHelper.addParam(inputUrl, "key with spaces", "value");
		assertEquals("Params with special characters should be url encoded", expected, actual);
		
	}
	
	public void testAddParam_withEmptyStrings () {
		
		String baseUrl = "http://beacondigital.us";
		String inputUrl, expected, actual;
		
		// Test empty value
		inputUrl = baseUrl;
		expected = inputUrl;
		actual = UrlHelper.addParam(inputUrl, "key", "");
		assertEquals("Passing an empty value should not add these params", expected, actual);
		
		// Test empty key
		inputUrl = baseUrl;
		expected = inputUrl;
		actual = UrlHelper.addParam(inputUrl, "", "value");
		assertEquals("Passing an empty key should not add these params", expected, actual);
		
		// Test null key
		inputUrl = baseUrl;
		expected = inputUrl;
		actual = UrlHelper.addParam(inputUrl, null, "value");
		assertEquals("Passing a null key should not add these params", expected, actual);
		
		// Test null value
		inputUrl = baseUrl;
		expected = inputUrl;
		actual = UrlHelper.addParam(inputUrl, "key", null);
		assertEquals("Passing a null value should not add these params", expected, actual);
		
	}
	
	public void testAddParam_encodingFlag () {
		
		String baseUrl = "http://beacondigital.us";
		String inputUrl, expected, actual;
		
		// Test that url encoding is on by default
		inputUrl = baseUrl;
		
		expected = inputUrl + "?key%20with%20spaces=value";
		actual = UrlHelper.addParam(inputUrl, "key with spaces", "value");
		assertEquals("Params key with special characters should be url encoded by default", expected, actual);
		
		expected = inputUrl + "?key=value%20with%20spaces";
		actual = UrlHelper.addParam(inputUrl, "key", "value with spaces");
		assertEquals("Params value with special characters should be url encoded by default", expected, actual);
		
		// Test that encoding can be turned off by flag
		inputUrl = baseUrl;
		
		expected = inputUrl + "?key with spaces=value";
		actual = UrlHelper.addParam(inputUrl, "key with spaces", "value", false);
		assertEquals("Params key with special characters should not be url encoded when flag is false", expected, actual);
		
		expected = inputUrl + "?key=value with spaces";
		actual = UrlHelper.addParam(inputUrl, "key", "value with spaces", false);
		assertEquals("Params value with special characters should not be url encoded when flag is false", expected, actual);
		
		
	}
	
}
