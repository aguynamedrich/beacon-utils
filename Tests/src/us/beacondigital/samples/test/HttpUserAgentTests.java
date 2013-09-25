package us.beacondigital.samples.test;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import us.beacondigital.utils.StringUtils;
import us.beacondigital.utils.net.HttpClientProvider;
import android.test.AndroidTestCase;

public class HttpUserAgentTests extends AndroidTestCase {
	
	public void testUserAgentString() {
		String defaultUserAgent = System.getProperty("http.agent");
		assertFalse(StringUtils.isNullOrEmpty(defaultUserAgent));
		
		DefaultHttpClient client = HttpClientProvider.get();
		String userAgent = (String) client.getParams().getParameter(HTTP.USER_AGENT);
		assertFalse(StringUtils.isNullOrEmpty(userAgent));
		
		String customUserAgent = "My Custom UserAgent string";
		HttpClientProvider.setUserAgent(customUserAgent);
		client = HttpClientProvider.get();
		userAgent = (String) client.getParams().getParameter(HTTP.USER_AGENT);
		assertFalse(StringUtils.isNullOrEmpty(userAgent));
		assertEquals(userAgent, customUserAgent);
	}

}
