package us.beacondigital.utils;

import org.apache.http.HttpResponse;

/**
 * Shortcuts for determining if an HttpResponse is valid and to determine the range of its status code
 * @author Rich
 *
 */
public class HttpUtils {

	public static boolean isOK(HttpResponse response) {
		return
				response != null &&
				response.getStatusLine() != null &&
				response.getStatusLine().getStatusCode() / 100 == 2;
	}

	public static boolean isUnauthorized(HttpResponse response) {
		return
				response != null &&
				response.getStatusLine() != null &&
				response.getStatusLine().getStatusCode() / 100 == 4;
	}

}