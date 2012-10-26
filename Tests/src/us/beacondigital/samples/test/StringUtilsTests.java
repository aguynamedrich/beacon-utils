package us.beacondigital.samples.test;

import android.test.AndroidTestCase;
import us.beacondigital.utils.StringUtils;

public class StringUtilsTests extends AndroidTestCase {
	
	public void testEllipsize() {
		String input = "This is a test";
		String expectedLengthFour = "This...";
		String expectedLengthSeven = "This is...";
		
		String actual = null;

		actual = StringUtils.ellipsize(input, 4);
		assertEquals("Input should be ellipsized", expectedLengthFour, actual);
		
		actual = StringUtils.ellipsize(input, 7);
		assertEquals("Input should be ellipsized", expectedLengthSeven, actual);
		
		actual = StringUtils.ellipsize(input, 50);
		assertEquals("Input should be unchanged when length argument is greater than input length", input, actual);
	}

}
