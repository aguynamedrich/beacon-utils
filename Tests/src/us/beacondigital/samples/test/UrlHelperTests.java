package us.beacondigital.samples.test;

import us.beacondigital.utils.UrlHelper;
import android.test.AndroidTestCase;

public class UrlHelperTests extends AndroidTestCase {
	
	public void testAppParamMethod() {
		
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
	
}
