package us.beacondigital.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;

/**
 * Shortcuts for determining if an HttpResponse is valid and to determine the range of its status code
 * @author Rich
 *
 */
public class HttpUtils {
	
	/**
	 * Utility method for reading a byte array from an InputStream object
	 * @param response InputStream object retrieved from an HttpResponse
	 * @return String contents of stream
	 */
	public static byte[] readBytes(HttpResponse response)
	{
		byte[] bytes = null;
		InputStream is = null;
		ByteArrayOutputStream buffer = null;
		try {
			is = response.getEntity().getContent();
			buffer = new ByteArrayOutputStream();

			int bytesRead;
			byte[] data = new byte[1024 * 16];
			while ((bytesRead = is.read(data, 0, data.length)) != -1) {
				  buffer.write(data, 0, bytesRead);
			}
			bytes = buffer.toByteArray();
		}
		catch (Exception ex) { }
		finally {
			IOUtils.safeClose(is);
			IOUtils.safeClose(buffer);
		}
		return bytes;
	}

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