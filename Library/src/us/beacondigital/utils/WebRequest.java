package us.beacondigital.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Utility for making simple requests to a web uri.
 * There are several overloads of the {@link #read(String) read} method 
 * which very simply returns the String contents of a web request and the 
 * {@link #execute(AbstractHttpClient, String) execute} method which returns
 * the {@link HttpResponse} object so the consumer can work with more than just
 * the text content of the response (such as cookies, headers and HTTP status codes)
 * @author Rich
 *
 */
public class WebRequest {
	
	private static final String ENCODING_UTF8 = "utf-8";
	
	public enum Verb {
		GET,
		POST,
		PUT,
		HEAD,
		DELETE
	}
	
	public enum AuthType {
		Basic,
		Digest
	}
	
	/**
	 * Performs a GET request to the supplied URL.  No additional headers, cookies or data are sent with this request
	 * @param url
	 * @return
	 */
	public static HttpResponse execute(AbstractHttpClient client, String url) {
		return execute(client, url, Verb.GET, null, null, AuthType.Basic, null, null);
	}
	
	public static HttpResponse execute(AbstractHttpClient client, String url, Cookie... cookies) {
		return execute(client, url, Verb.GET, cookies, null, AuthType.Basic, null, null);
	}
	
	public static HttpResponse execute(AbstractHttpClient client, String url, Verb verb, Cookie... cookies) {
		return execute(client, url, verb, cookies, null, AuthType.Basic, null, null);
	}
	
	public static HttpResponse execute(AbstractHttpClient client, String url, NameValuePair... params) {
		return execute(client, url, Verb.GET, null, params, AuthType.Basic, null, null);
	}
	
	public static HttpResponse execute(AbstractHttpClient client, String url, Verb verb, NameValuePair... params) {
		return execute(client, url, verb, null, params, AuthType.Basic, null, null);
	}
	
	public static HttpResponse execute(AbstractHttpClient client, String url, Verb verb, Cookie[] cookies, NameValuePair[] params) {
		return execute(client, url, verb, cookies, params, AuthType.Basic, null, null);
	}
	
	/**
	 * This method is where the work is done, taking all possible parameters as arguments.
	 * There are several overloads that are much simpler when not all parameters are necessary
	 * @param url
	 * @param verb
	 * @param cookies
	 * @param params
	 * @param user
	 * @param pass
	 * @return
	 */
	public static HttpResponse execute(AbstractHttpClient client, String url, Verb verb, Cookie[] cookies, NameValuePair[] params, AuthType authType, String basicAuthUser, String basicAuthPass) {
		HttpResponse response = null;
		
		// Add cookies
		if (cookies != null && cookies.length > 0) {
			CookieStore cookieStore = new BasicCookieStore();
			for (Cookie cookie : cookies) {
				cookieStore.addCookie(cookie);
			}
			client.setCookieStore(cookieStore);
		}
		
		// Construct the appropriate request object, adding parameters where valid
		HttpUriRequest request = null;		
		switch (verb) {
		case GET:
			request = new HttpGet(url);
			addGetParams((HttpGet) request, params);
			break;
		case POST:
			request = new HttpPost(url);
			addPostParams((HttpPost) request, params);
			break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		case HEAD:
			request = new HttpHead(url);
			break;
		default:
			break;
		}
		
		// Add a auth header if parameters supplied
		// The method we call will short circuit if parameters are invalid
		addAuthenticationHeader(request, authType, basicAuthUser, basicAuthPass);
		
		try {
			response = client.execute(request);
		}
		catch (ClientProtocolException e) { }
		catch (IOException e) { }
		
		return response;
	}
	
	private static void addGetParams(HttpGet get, NameValuePair[] params) {
		if(params != null && params.length > 0) {
			String query = URLEncodedUtils.format(Arrays.asList(params), ENCODING_UTF8);
			try {
				get.setURI(new URI(get.getURI().toString() + query));
			}
			catch (URISyntaxException e) { }
		}
	}

	/**
	 * Safely add an array of NameValuePairs to a POST request
	 * @param request
	 * @param params
	 */
	private static void addPostParams(HttpPost post, NameValuePair[] params) {
		if(params != null && params.length > 0) {
			try {
				post.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**	 * 
	 * If all parameters are valid, adds an authorization header to the request object
	 * @param request
	 * @param authType
	 * @param basicAuthUser
	 * @param basicAuthPass
	 */
	private static void addAuthenticationHeader(HttpRequest request, AuthType authType, String basicAuthUser, String basicAuthPass) {
		if (!StringUtils.isNullOrEmpty(basicAuthUser) && !StringUtils.isNullOrEmpty(basicAuthPass)) {
			Header authorizationHeader = null;
			try {
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basicAuthUser, basicAuthPass);
				AuthSchemeBase scheme = null;
				switch (authType) {
				case Basic:
					scheme = new BasicScheme();
					break;
				case Digest:
					scheme = new DigestScheme();
					break;
				}
				authorizationHeader = scheme.authenticate(credentials, request);
			}
			catch (AuthenticationException e) { }
			if (authorizationHeader != null)
				request.addHeader(authorizationHeader);
		}
	}
	
	/**	 * 
	 * This method returns only the String contents of a web request.  Use 
	 * {@link #execute(AbstractHttpClient, String) execute}
	 * if you need more, such as the {@link StatusLine} or {@link CookieStore}
	 * @param url
	 * @return
	 */
	public static String read(String url) {
		return read(url, Verb.GET, null, null, AuthType.Basic, null, null);
	}
	
	public static String read(String url, Cookie... cookies) {
		return read(url, Verb.GET, cookies, null, AuthType.Basic, null, null);
	}
	
	public static String read(String url, Verb verb, Cookie... cookies) {
		return read(url, verb, cookies, null, AuthType.Basic, null, null);
	}
	
	public static String read(String url, NameValuePair... params) {
		return read(url, Verb.GET, null, params, AuthType.Basic, null, null);
	}
	
	public static String read(String url, Verb verb, NameValuePair... params) {
		return read(url, verb, null, params, AuthType.Basic, null, null);
	}
	
	public static String read(String url, Verb verb, Cookie[] cookies, NameValuePair[] params) {
		return read(url, verb, cookies, params, AuthType.Basic, null, null);
	}
	
	/**
	 * This method returns only the String contents of a web request.  Use 
	 * {@link #execute(AbstractHttpClient, String, Verb, Cookie[], NameValuePair[], AuthType, String, String) execute}
	 * if you need more, such as the {@link StatusLine} or {@link CookieStore}
	 * @param url
	 * @param verb
	 * @param cookies
	 * @param params
	 * @param authType
	 * @param basicAuthUser
	 * @param basicAuthPass
	 * @return
	 */
	public static String read(String url, Verb verb, Cookie[] cookies, NameValuePair[] params, AuthType authType, String basicAuthUser, String basicAuthPass) {
		DefaultHttpClient client = HttpClientProvider.get();
		HttpResponse response = execute(client, url, verb, cookies, params, authType, basicAuthUser, basicAuthPass);
		String data = StringUtils.readStream(response);
		IOUtils.safeClose(client);
		return data;
	}
	
	/**	 * 
	 * This method returns only the String contents of a web request.  Use 
	 * {@link #execute(AbstractHttpClient, String) execute}
	 * if you need more, such as the {@link StatusLine} or {@link CookieStore}
	 * @param url
	 * @return
	 */
	public static byte[] readBytes(String url) {
		return readBytes(url, Verb.GET, null, null, AuthType.Basic, null, null);
	}
	
	public static byte[] readBytes(String url, Cookie... cookies) {
		return readBytes(url, Verb.GET, cookies, null, AuthType.Basic, null, null);
	}
	
	public static byte[] readBytes(String url, Verb verb, Cookie... cookies) {
		return readBytes(url, verb, cookies, null, AuthType.Basic, null, null);
	}
	
	public static byte[] readBytes(String url, NameValuePair... params) {
		return readBytes(url, Verb.GET, null, params, AuthType.Basic, null, null);
	}
	
	public static byte[] readBytes(String url, Verb verb, NameValuePair... params) {
		return readBytes(url, verb, null, params, AuthType.Basic, null, null);
	}
	
	public static byte[] readBytes(String url, Verb verb, Cookie[] cookies, NameValuePair[] params) {
		return readBytes(url, verb, cookies, params, AuthType.Basic, null, null);
	}
	
	/**
	 * This method returns only the String contents of a web request.  Use 
	 * {@link #execute(AbstractHttpClient, String, Verb, Cookie[], NameValuePair[], AuthType, String, String) execute}
	 * if you need more, such as the {@link StatusLine} or {@link CookieStore}
	 * @param url
	 * @param verb
	 * @param cookies
	 * @param params
	 * @param authType
	 * @param basicAuthUser
	 * @param basicAuthPass
	 * @return
	 */
	public static byte[] readBytes(String url, Verb verb, Cookie[] cookies, NameValuePair[] params, AuthType authType, String basicAuthUser, String basicAuthPass) {
		DefaultHttpClient client = HttpClientProvider.get();
		HttpResponse response = execute(client, url, verb, cookies, params, authType, basicAuthUser, basicAuthPass);
		byte[] data = HttpUtils.readBytes(response);
		IOUtils.safeClose(client);
		return data;
	}
}
