package us.beacondigital.utils;

public class ImageInfoValidator {

	public static boolean isValid(ImageInfo info) {
		return
				info != null &&
				StringUtils.isValidUrl(info.getUrl()) &&
				info.getDescriptor() != null &&
				info.getDescriptor().isValid();
	}

}
