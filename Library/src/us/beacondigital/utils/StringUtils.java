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
	
	/**
	 * Tests an input string against the null value or trimmed length greater than zero
	 * @param input
	 * @return
	 */
	public static boolean isNullOrEmpty(String input)
	{
		return input == null || input.trim().length() == 0;
	}
	
	/**
	 * Tests an input string for a valid email address against the following regular expression
	 * {@value #EmailRegex}
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {

		if(StringUtils.isNullOrEmpty(email))
		{
			return false;
		}

		return email.matches(EmailRegex);
	}
	
	/**
	 * Tests an input string for a valid url pattern using the following regular expression:
	 * {@value #URLRegex}
	 * @param url
	 * @return
	 */
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

	/**
	 * Tests first against a regular expression that the input string is all digits
	 * and then checks that the parsed value is greater than zero (zero value is
	 * not positive but passes the regular expression test)
	 * @param input
	 * @return
	 */
	public static boolean isPositiveInteger(String input) {

		if(StringUtils.isNullOrEmpty(input))
		{
			return false;
		}

		return input.matches(AllDigitsRegex) && Integer.parseInt(input) > 0;
		
	}

	/**
	 * Tests an input string can be parsed to a numeric data type
	 * using the following regular expression:
	 * {@value #NumericRegex}
	 * @param input
	 * @return
	 */
	public static boolean isNumeric(String input) {

		if(StringUtils.isNullOrEmpty(input))
		{
			return false;
		}

		return input.matches(NumericRegex);
		
	}
	
	public static String ellipsize(String input, int length) {
		if(input.length() <= length)
			return input;
		else
			return String.format("%s...", input.substring(0, length));
	}
	
	/**
	* Shorthand for appending a String to a StringBuilder with formatting defined inline
	* @param sb
	* @param format
	* @param params
	*/
	public static void appendWithFormat(StringBuilder sb, String format, Object... params)
	{
		sb.append(String.format(format, params));
	}

	/**
	* Overloaded method for including non parametric format strings such as newline
	* @param sb
	* @param format
	*/
	public static void appendWithFormat(StringBuilder sb, String format)
	{
		sb.append(String.format(format));
	}

}
