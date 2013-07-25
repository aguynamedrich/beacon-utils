package us.beacondigital.utils.image;

import us.beacondigital.utils.StringUtils;

public class ImageInfoValidator {

	public static boolean isValid(ImageInfo info) {
		return
				info != null &&
				StringUtils.isValidUrl(info.getUrl()) &&
				info.getDescriptor() != null &&
				info.getDescriptor().isValid();
	}

}
