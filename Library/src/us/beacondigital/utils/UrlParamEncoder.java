package us.beacondigital.utils;

public class UrlParamEncoder {

    public static String encode(String input) {
        StringBuilder encoded = new StringBuilder();
        for (char ch : input.toCharArray()) {
        	
            if (isUnsafe(ch)) {
            	encoded.append('%');
            	encoded.append(toHex(ch / 16));
            	encoded.append(toHex(ch % 16));
            } else {
            	encoded.append(ch);
            }
        }
        return encoded.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
    	boolean isUnsafe = true;
        if (ch > 128 || ch < 0) {
        	isUnsafe = true;
        }
        else if (" %$&+,/:;=?@<>#%".indexOf(ch) >= 0) {
        	isUnsafe = true;
        }
        return isUnsafe;
    }

}
