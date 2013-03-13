package us.beacondigital.utils;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;

/**
 * Wrapper class for easier implementation of common HTTP related functionality.
 * Provides convenience methods for pulling usable objects such as Strings and Bitmaps from framework objects 
 * such as streams and http response wrappers.  Also provides HTTP communication helpers.
 * @author Rich Stern
 *
 */
public class HttpHelper
{	
	public final static String HeaderLastModified = "Last-Modified";
	public final static String HeaderContentLength = "Content-Length";
	public final static String HeaderContentType = "Content-Type";
	public final static String HeaderAccept = "Accept";

	public final static String ContentTypeJson = "application/json";
	public final static String ContentTypeJpeg = "image/jpeg";
	public final static String ContentTypeMultipartFormData = "multipart/form-data";
	
	/**
	 * Utility method for building a Bitmap object out of the text received from a url
	 * @param url - The address of the resource
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return An Android Bitmap object
	 */
	public static Bitmap getImage(String url, HttpClient client)
	{
		Bitmap bmp = null;
		try
		{
			InputStream inputStream = getResponse(url, client).getEntity().getContent();
			bmp = BitmapFactory.decodeStream(inputStream);
		}
		catch(Exception ex) { }
		return bmp;
	}
	
	/**
	 * Simplest way to 
	 * @param url
	 * @return
	 */
	public static Bitmap getImage(String url) {
		DefaultHttpClient client = HttpClientProvider.get();
		Bitmap image = getImage(url, client);
		IOUtils.safeClose(client);
		return image;
	}
	
	public static Header getBasicAuthHeader(String user, String password, HttpRequest request) {
		Header authorizationHeader = null;
		try {
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
			BasicScheme scheme = new BasicScheme();
			authorizationHeader = scheme.authenticate(credentials, request);
		}
		catch (AuthenticationException e) { }
		return authorizationHeader;
	}
	
	/**
	 * Simplest possible way to read a string from a url using a single-use DefaultHttpClient
	 * @param url
	 * @return
	 */
	public static String read(String url) {
		DefaultHttpClient client = HttpClientProvider.get();
		String body = getText(url, client);
		IOUtils.safeClose(client);
		return body;
	}
	
	/**
	 * Direct call for consumers to get String data from a HTTP GET request
	 * @param url
	 * @param context
	 * @return
	 */
	public static String getText(String url, HttpClient client)
	{
		return StringUtils.readStream(getResponse(url, client));
	}
	
	/**
	 * Overload of GetResponse that bypasses basic http authentication by passing username and password params as null
	 * @param url - The address of the resource
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return HttpResponse object containing the headers requested
	 */
	public static HttpResponse getResponse(String url, HttpClient client)
	{
		return getResponse(url, null, null, client);
	}
	
	/**
	 * Perform an HTTP GET request
	 * If basic http authentication is not required, there is an overload that does not take username and password params
	 * @param url - The address of the resource
	 * @param authUser - Authentication user name if using basic http authentication
	 * @param authPass - Authentication password if using basic http authentication
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return HttpResponse object representing the resource requested
	 */
	public static HttpResponse getResponse(String url, String authUser, String authPass, HttpClient client)
	{
		HttpResponse response = null;
		try
		{
			HttpGet request = new HttpGet(url);
			addBasicAuthHeader(request, authUser, authPass);
		    response = client.execute(request);
		}
		catch(Exception ex) { }
		return response;
	}
	
	/**
	 * Post a collection of key-value pairs and return and HttpResponse
	 * @param url - The address of the resource
	 * @param params - The key/value pairs of data
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return HttpResponse object returned from this request
	 */
	public static HttpResponse post(String url, List<NameValuePair> params, List<NameValuePair> headers, HttpClient client)
	{
		HttpResponse response = null;
		try
		{
			HttpPost request = new HttpPost();
			// add headers
			if(headers != null) {
				for(NameValuePair header : headers) {
					request.addHeader(header.getName(), header.getValue());
				}
			}
			request.setURI(new URI(url));
			if(params != null)
				request.setEntity(new UrlEncodedFormEntity(params));
		    response = client.execute(request);
		}
		catch(Exception ex) { }
		return response;
	}
	
	/**
	 * Utility method for returning the value received from an HTTP HEAD request
	 * @param headerKey - The key of the HTTP header to request
	 * @param url - The address of the resource
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return Header value as a String, or null if the request failed or returned no value
	 */
	public static String getHeaderValue(String headerKey, String url, HttpClient client)
	{
		String headerValue = null;
		HttpResponse response = getHeaders(url, client);
		if(response != null)
		{
			Header header = response.getFirstHeader(headerKey);
			if(header != null)
				headerValue = header.getValue();
		}
		return headerValue;
	}

	/**
	 * Overload for retrieving a header value from calling code that already has an HttpResponse object
	 * @param headerKey
	 * @param response
	 * @return
	 */
	public static String getHeaderValue(String headerKey, HttpResponse response)
	{
		String headerValue = null;
		if(response != null)
		{
			Header header = response.getFirstHeader(headerKey);
			if(header != null)
				headerValue = header.getValue();
		}
		return headerValue;
	}

	/**
	 * Overload for retrieving a header value as long from calling code that already has an HttpResponse object
	 * @param headerKey
	 * @param response
	 * @return
	 */
	public static long getHeaderValueAsLong(String headerKey, HttpResponse response)
	{
		long longValue = 0;
		String headerValue = null;
		if(response != null)
		{
			Header header = response.getFirstHeader(headerKey);
			if(header != null)
				headerValue = header.getValue();
		}
		if(!StringUtils.isNullOrEmpty(headerValue))
			longValue = Long.valueOf(headerValue);
		return longValue;
	}
	
	/**
	 * Overload of GetHeaders that bypasses basic http authentication by passing username and password params as null
	 * @param url - The address of the resource
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return HttpResponse object containing the headers requested
	 */
	public static HttpResponse getHeaders(String url, HttpClient client)
	{
		return getHeaders(url, null, null, client);
	}
	
	/**
	 * Performs an HTTP HEAD request so we can read file headers without having to incur the overhead of downloading the whole file.
	 * If basic http authentication is not required, there is an overload that does not take username and password params
	 * @param url - The address of the resource
	 * @param authUser - Authentication user name if using basic http authentication
	 * @param authPass - Authentication password if using basic http authentication
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return HttpResponse object containing the headers requested
	 */
	public static HttpResponse getHeaders(String url, String authUser, String authPass, HttpClient client)
	{
		HttpResponse response = null;
		try
		{
			HttpHead request = new HttpHead(url);
			addBasicAuthHeader(request, authUser, authPass);
		    response = client.execute(request);
		}
		catch(Exception ex) { }
		return response;
	}
	
	/**
	 * Gets the underlying stream from an HttpResponse object
	 * @param url - The address of the resource
	 * @param context - Context such as an activity or application context for retrieving the single instance HttpClient of the app
	 * @return InputStream object
	 */
	public static InputStream getInputStream(String url, HttpClient client)
	{
		InputStream stream = null;
		try
		{
			stream = getResponse(url, client).getEntity().getContent();
		}
		catch(Exception ex) { }
		return stream;
	}

	public static HttpResponse put(AndroidHttpClient client, String url, List<NameValuePair> headers, String payload) {
		HttpResponse response = null;
		try
		{
			HttpPut request = new HttpPut();
			request.setURI(new URI(url));
			request.setEntity(new StringEntity(payload));
			
			// add headers
			if(headers != null) {
				for(NameValuePair header : headers) {
					request.addHeader(header.getName(), header.getValue());
				}
			}
			
			response = client.execute(request);
		}
		catch(Exception ex) { }
		return response;
	}
	
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
	
	public static void addBasicAuthHeader(HttpRequest request, String user, String pass) {
		if (!StringUtils.isNullOrEmpty(user) && !StringUtils.isNullOrEmpty(pass)) {
			Header authHeader = getBasicAuthHeader(user, pass, request);
			if (authHeader != null)
				request.addHeader(authHeader);
		}
	}
}