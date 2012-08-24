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
	public static String addParam(String url, String paramKey, String paramValue, boolean encodingOn) {
		String newUrl;
		if(StringUtils.isNullOrEmpty(paramKey) || StringUtils.isNullOrEmpty(paramValue)) {
			newUrl = url;
		}
		else {
			boolean isFirst = url.indexOf("?") == -1;
			String separator = isFirst ? "?" : "&";
			String key = encodingOn ? UrlParamEncoder.encode(paramKey) : paramKey;
			String value = encodingOn ? UrlParamEncoder.encode(paramValue) : paramValue;
			
			newUrl = String.format("%s%s%s=%s",
					url,
					separator,
					key,
					value);
		}
		return newUrl;
	}
	
	public static String addParam(String url, String paramKey, String paramValue) {
		return addParam(url, paramKey, paramValue, true);
	}
	
	public static String addParam(String url, String paramKey, int paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}
	
	public static String addParam(String url, String paramKey, long paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}
	
	public static String addParam(String url, String paramKey, double paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}
	
	public static String addParam(String url, String paramKey, float paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}
	
	public static String addParam(String url, String paramKey, boolean paramValue) {
		return addParam(url, paramKey, String.valueOf(paramValue));
	}

}
