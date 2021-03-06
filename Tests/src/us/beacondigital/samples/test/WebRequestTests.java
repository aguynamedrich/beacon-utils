package us.beacondigital.samples.test;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import us.beacondigital.utils.IOUtils;
import us.beacondigital.utils.net.HttpClientProvider;
import us.beacondigital.utils.net.WebRequest;
import android.test.AndroidTestCase;

public class WebRequestTests extends AndroidTestCase {
	
	public void testBasicRequests() {
		DefaultHttpClient client = HttpClientProvider.get();
		
		String url = "http://www.google.com";
		HttpResponse response = WebRequest.execute(client, url);
		
		assertTrue(response.getStatusLine().getStatusCode() == 200);
		
		IOUtils.safeClose(client);
	}

}
