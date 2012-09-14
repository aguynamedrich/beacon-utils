package us.beacondigital.samples.test;

import us.beacondigital.utils.StringUtils;
import android.test.AndroidTestCase;

public class StringUtilsNumericTests extends AndroidTestCase {
	
	/**
	 * Test a variety of invalid and malformed numeric strings against a string positive integer regular expression
	 */
	public void testPositiveIntegerRegex() {
		String positiveInteger = "123";
		String zero = "0";
		String zeroInDecimal = "0.0";
		String nullString = null;
		String emptyString = "";
		String negativeInteger = "-123";
		String paddedPositiveInteger = " 123 ";
		String paddedNegativeInteger = " -123 ";
		String positiveDecimal = "123.456";
		String negativeDecimal = "-1.23";
		String letters = "abc";
		String mixedPrefixedNumeric = "123abc";
		String mixedPostfixedNumeric = "abc123";
		
		assertTrue("Positive integer should evaluate true", StringUtils.isPositiveInteger(positiveInteger));

		assertFalse("zero should evaluate false", StringUtils.isPositiveInteger(zero));
		assertFalse("zeroInDecimal should evaluate false", StringUtils.isPositiveInteger(zeroInDecimal));
		assertFalse("Null string should evaluate false", StringUtils.isPositiveInteger(nullString));
		assertFalse("Empty string should evaluate false", StringUtils.isPositiveInteger(emptyString));
		assertFalse("negativeInteger should evaluate false", StringUtils.isPositiveInteger(negativeInteger));
		assertFalse("paddedPositiveInteger should evaluate false", StringUtils.isPositiveInteger(paddedPositiveInteger));
		assertFalse("paddedNegativeInteger should evaluate false", StringUtils.isPositiveInteger(paddedNegativeInteger));
		assertFalse("positiveDecimal should evaluate false", StringUtils.isPositiveInteger(positiveDecimal));
		assertFalse("negativeDecimal should evaluate false", StringUtils.isPositiveInteger(negativeDecimal));
		assertFalse("letters should evaluate false", StringUtils.isPositiveInteger(letters));
		assertFalse("mixedPrefixedNumeric should evaluate false", StringUtils.isPositiveInteger(mixedPrefixedNumeric));
		assertFalse("mixedPostfixedNumeric should evaluate false", StringUtils.isPositiveInteger(mixedPostfixedNumeric));
	}
	
	public void testIsNumericRegex() {
		String positiveInteger = "123";
		String zero = "0";
		String zeroInDecimal = "0.0";
		String nullString = null;
		String emptyString = "";
		String negativeInteger = "-123";
		String paddedPositiveInteger = " 123 ";
		String paddedNegativeInteger = " -123 ";
		String positiveDecimal = "123.456";
		String negativeDecimal = "-1.23";
		String letters = "abc";
		String mixedPrefixedNumeric = "123abc";
		String mixedPostfixedNumeric = "abc123";
		
		assertTrue("Positive integer should evaluate true", StringUtils.isNumeric(positiveInteger));
		assertTrue("zero should evaluate true", StringUtils.isNumeric(zero));
		assertTrue("zeroInDecimal should evaluate true", StringUtils.isNumeric(zeroInDecimal));
		assertTrue("negativeInteger should evaluate true", StringUtils.isNumeric(negativeInteger));
		assertTrue("positiveDecimal should evaluate true", StringUtils.isNumeric(positiveDecimal));
		assertTrue("negativeDecimal should evaluate true", StringUtils.isNumeric(negativeDecimal));

		assertFalse("Null string should evaluate false", StringUtils.isNumeric(nullString));
		assertFalse("Empty string should evaluate false", StringUtils.isNumeric(emptyString));
		assertFalse("paddedPositiveInteger should evaluate false", StringUtils.isNumeric(paddedPositiveInteger));
		assertFalse("paddedNegativeInteger should evaluate false", StringUtils.isNumeric(paddedNegativeInteger));
		assertFalse("letters should evaluate false", StringUtils.isNumeric(letters));
		assertFalse("mixedPrefixedNumeric should evaluate false", StringUtils.isNumeric(mixedPrefixedNumeric));
		assertFalse("mixedPostfixedNumeric should evaluate false", StringUtils.isNumeric(mixedPostfixedNumeric));
	}

}
