package us.beacondigital.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class StringUtils {
	
	private final static String EmailRegex = "^[_a-z0-9-+]+(\\.[_a-z0-9-+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,8})$";
	private final static String URLRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private final static String AllDigitsRegex = "^\\d+$";
	private final static String NumericRegex = "^-?\\d*(\\.\\d+)?$";
	
	/**
	 * Utility method for pulling plain text from an InputStream object
	 * @param in InputStream object retrieved from an HttpResponse
	 * @return String contents of stream
	 */
	public static String readStream(InputStream in)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}
		catch(Exception ex) { }
		finally
		{
			IOUtils.safeClose(in);
			IOUtils.safeClose(reader);
		}
		return sb.toString();
	}
	
	public static boolean isNullOrEmpty(String input)
	{
		return input == null || input.trim().length() == 0;
	}
	
	public static boolean isValidEmail(String email) {

		if(StringUtils.isNullOrEmpty(email))
		{
			return false;
		}

		return email.matches(EmailRegex);
	}
	
	public static boolean isValidUrl(String url) {

		if(StringUtils.isNullOrEmpty(url))
		{
			return false;
		}

		return url.matches(URLRegex);
	}

	public static String readStream(HttpResponse response) {
		String data = null;
		try
		{
			data = readStream(response.getEntity().getContent());
		}
		catch(Exception ex) { }
		return data;
	}

	public static boolean isPositiveInteger(String input) {

		if(StringUtils.isNullOrEmpty(input))
		{
			return false;
		}

		return input.matches(AllDigitsRegex) && Integer.parseInt(input) > 0;
		
	}

	public static boolean isNumeric(String input) {

		if(StringUtils.isNullOrEmpty(input))
		{
			return false;
		}

		return input.matches(NumericRegex);
		
	}

}
