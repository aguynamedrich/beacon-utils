package us.beacondigital.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
	
	/**
	 * Returns an integer from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and a default value of 0 is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static int getInt(JSONObject obj, String name)
	{
		return getInt(obj, name, 0);
	}

	/**	 * 
	 * Returns an integer from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and the supplied default value is returned.
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(JSONObject obj, String name, int defaultValue) {
		int val = defaultValue;
		try
		{
			val = obj.getInt(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		return val;
	}
	
	/**
	 * Returns an String from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and a default value of null is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static String getString(JSONObject obj, String name)
	{
		return getString(obj, name, null);
	}

	/**
	 * Returns an String from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and the supplied default value is returned.
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getString(JSONObject obj, String name, String defaultValue) {
		String val = defaultValue;
		try
		{
			if (!obj.isNull(name))
				val = obj.getString(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		return val;
	}
	
	/**
	 * Returns a double from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and a default value of 0 is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static double getDouble(JSONObject obj, String name)
	{
		return getDouble(obj, name, 0);
	}

	/**
	 * Returns a double from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and the supplied default value is returned.
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static double getDouble(JSONObject obj, String name, double defaultValue) {
		double val = defaultValue;
		try
		{
			val = obj.getDouble(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		return val;
	}

	/**
	 * Returns a boolean from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and a default value of false is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static boolean getBoolean(JSONObject obj, String name) {
		return getBoolean(obj, name, false);
	}

	/**
	 * Returns a boolean from a JSON object with the given key.  If the key does not exist or the JSON object is null,
	 * the exception is swallowed and the supplied default value is returned.
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(JSONObject obj, String name, boolean defaultValue) {
		boolean val = defaultValue;
		try
		{
			val = obj.getBoolean(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		return val;
	}
	
	/**
	 * Returns a child JSONObject from an outer object with the given key.  If the key does not exist or the outer object is null,
	 * the exception is swallowed and a default value of null is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static JSONObject getJSONObject(JSONObject obj, String name) {
		JSONObject val = null;
		try
		{
			val = obj.getJSONObject(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		
		return val;
	}
	
	/**
	 * Returns a JSONArray from an outer JSONObject with the given key.  If the key does not exist or the outer object is null,
	 * the exception is swallowed and a default value of null is returned.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static JSONArray getJSONArray(JSONObject obj, String name) {
		JSONArray val = null;
		try
		{
			val = obj.getJSONArray(name);
		}
		catch (JSONException e) { }
		catch (NullPointerException e) { }
		
		return val;
	}
	
	public static void put(JSONObject obj, String name, String value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}
	
	public static void put(JSONObject obj, String name, int value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}
	
	public static void put(JSONObject obj, String name, long value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}
	
	public static void put(JSONObject obj, String name, double value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}
	
	public static void put(JSONObject obj, String name, float value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}
	
	public static void put(JSONObject obj, String name, boolean value) {
		if(obj != null && !StringUtils.isNullOrEmpty(name)) {
			try {
				obj.put(name, value);
			}
			catch (JSONException e) { }
		}
	}

}
