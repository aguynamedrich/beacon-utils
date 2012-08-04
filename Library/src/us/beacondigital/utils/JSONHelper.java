package us.beacondigital.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
	
	public static int getInt(JSONObject obj, String name)
	{
		return getInt(obj, name, 0);
	}

	public static int getInt(JSONObject obj, String name, int defaultValue) {
		int val = defaultValue;
		try
		{
			val = obj.getInt(name);
		}
		catch (JSONException e) { }
		return val;
	}
	
	public static String getString(JSONObject obj, String name)
	{
		return getString(obj, name, null);
	}

	public static String getString(JSONObject obj, String name, String defaultValue) {
		String val = defaultValue;
		try
		{
			val = obj.getString(name);
		}
		catch (JSONException e) { }
		return val;
	}
	
	public static double getDouble(JSONObject obj, String name)
	{
		return getDouble(obj, name, 0);
	}

	public static double getDouble(JSONObject obj, String name, double defaultValue) {
		double val = defaultValue;
		try
		{
			val = obj.getDouble(name);
		}
		catch (JSONException e) { }
		return val;
	}

	public static boolean getBoolean(JSONObject obj, String name) {
		return getBoolean(obj, name, false);
	}

	public static boolean getBoolean(JSONObject obj, String name, boolean defaultValue) {
		boolean val = defaultValue;
		try
		{
			val = obj.getBoolean(name);
		}
		catch (JSONException e) { }
		return val;
	}

}
