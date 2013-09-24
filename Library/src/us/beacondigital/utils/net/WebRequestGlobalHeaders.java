package us.beacondigital.utils.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

/**
 * Maintains a collection of HTTP Headers that should be added to 
 * all HTTP requests that go through the WebRequest class
 * @author rich
 *
 */
public class WebRequestGlobalHeaders {
	
	private static List<Header> headers = new ArrayList<Header>();
	
	public static void clear() {
		if (headers != null)
			headers.clear();
	}
	
	public static void add(Header header) {
		add(header, true);
	}
	
	public static void add(Header header, boolean replaceIfExists) {
		if (headers != null) {
			if (!has(header)) {
				headers.add(header);
			}
			else if (replaceIfExists) {
				// remove header, then add
				int position = getPosition(header);
				if (position > 0 && position < headers.size()){
					headers.remove(position);
					headers.add(header);
				}
			}
		}
	}
	
	private static int getPosition(Header header) {
		int position = -1;
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).getName().equals(header.getName())) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * Check if headers collection already has a header with this name
	 * @param header
	 * @return
	 */
	private static boolean has(Header header) {
		boolean hasHeader = false;
		if (headers != null) {
			for (Header h : headers) {
				if (h.getName().equals(header.getName())) {
					hasHeader = true;
					break;
				}
			}
		}
		return hasHeader;
	}

	public static List<Header> get() {
		return headers;
	}

}
