package us.beacondigital.utils.net;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;

public class WebRequestHelper {
	
	/**
	 * Get the String contents of a cookie by name if it exists
	 * @param client
	 * @param name
	 * @return
	 */
	public static String getCookieValue(AbstractHttpClient client, String name) {
		String value = null;
		CookieStore cookieStore = client.getCookieStore();
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				value = cookie.getValue();
			}
		}
		return value;
	}

	public static Cookie getCookie(AbstractHttpClient client, String name) {
		Cookie value = null;
		CookieStore cookieStore = client.getCookieStore();
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				value = cookie;
			}
		}
		return value;
	}

}
