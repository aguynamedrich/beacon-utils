package us.beacondigital.utils;

public class UrlHelper {
	
	/**
	 * Add GET param to an existing URL
	 * @param url
	 * @param paramKey
	 * @param paramValue
	 * @param isFirst
	 * @return
	 */
	public static String addParam(String url, String paramKey, String paramValue) {
		boolean isFirst = url.indexOf("?") > -1;
		String newUrl = String.format("%s%s%s=%s",
				url,
				((isFirst) ? "?" : "&"),
				UrlParamEncoder.encode(paramKey),
				UrlParamEncoder.encode(paramValue));
		return newUrl;
	}
	
	public static String addParam(String url, String paramKey, int paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}
	
	public static String addParam(String url, String paramKey, double paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}

}
