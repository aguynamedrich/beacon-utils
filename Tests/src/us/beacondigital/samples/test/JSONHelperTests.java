package us.beacondigital.samples.test;

import org.json.JSONException;
import org.json.JSONObject;

import us.beacondigital.utils.JSONHelper;

import android.test.AndroidTestCase;

public class JSONHelperTests extends AndroidTestCase {

	public void testGetJSONObject() {
		String objectKey = "test_object_key";
		
		JSONObject objectNull = null;
		JSONObject objectEmpty = new JSONObject();
		JSONObject objectInvalid = new JSONObject();
		try {
			objectInvalid.put(objectKey, 42);
		}
		catch (JSONException e) { }
		JSONObject objectValid = new JSONObject();
		try {
			objectValid.put(objectKey, new JSONObject());
		}
		catch (JSONException e) { }
		
		JSONObject result = JSONHelper.getJSONObject(objectNull, objectKey);
		assertNull("Resulting object should be null when queried from null outer JSON object", result);
		
		result = JSONHelper.getJSONObject(objectEmpty, objectKey);
		assertNull("Resulting object should be null when queried from JSON object not containing our key", result);
		
		result = JSONHelper.getJSONObject(objectInvalid, objectKey);
		assertNull("Resulting object should be null when queried from JSON object with wrong type of value for our key", result);
		
		result = JSONHelper.getJSONObject(objectValid, objectKey);
		assertNotNull("Resulting object should not be null when queried from outer object containing a JSONObject with our key", result);
		assertTrue("Resulting object should be of type JSONObject when queried properly from JSONHelper", result.getClass().equals(JSONObject.class));
	}
	
	/**
	 * This test is to make sure that the getString method takes into account
	 * null values.  By default { "key": null } will parse to the string "null"
	 * when using JSONObject.getString by itself.  To get around that, we test 
	 * JSONObject.isNull(key) before attempting to retrieve
	 * @throws JSONException 
	 */
	public void testNullValueForStringField() throws JSONException {
		String jsonWithNull = "{ key: null }";
		String jsonWithValue = "{ key: \"some value\" }";
		JSONObject objWithNull = new JSONObject(jsonWithNull);
		JSONObject objWithValue = new JSONObject(jsonWithValue);
		
		assertNull(JSONHelper.getString(objWithNull, "key"));
		assertNotNull(JSONHelper.getString(objWithValue, "key"));
	}

}
