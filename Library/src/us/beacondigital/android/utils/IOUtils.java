package us.beacondigital.android.utils;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.HttpClient;

import android.database.Cursor;

public class IOUtils {
	
	public static void safeClose(Closeable closeable)
	{
		if(closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (IOException e) { }
		}
	}
	
	public static void safeClose(HttpClient client)
	{
		if(client != null && client.getConnectionManager() != null)
		{
			client.getConnectionManager().shutdown();
		}
	}

	public static void safeClose(Cursor c) {
		if(c != null)
			c.close();
	}

}
