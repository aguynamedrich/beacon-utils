package us.beacondigital.utils;

import android.graphics.Bitmap.CompressFormat;

/**
 * This is a class that represents meta data about a remote image.  The consumer of this class
 * (typically an Android activity) builds an instance of this object and its inner class
 * ImageDescriptor and passes it to a RemoteImageView.  The meta data is used to manage the 
 * downloading of remote image data and optionally caching the image for subsequent use.
 * @author Rich Stern
 *
 */
public class ImageInfo {
	
	private static final int DefaultQuality = 75;
	
	public enum UniqueIdType {
		LongInt,
		String
	}
	
	public enum Extension {
		None,
		Jpg,
		Png
	}
	
	/**
	 * This class uses a description of the image as well as a unique id to 
	 * create unique file names for uniquely defined image info
	 * @author Rich Stern
	 *
	 */
	public static class ImageDescriptor {
		
		private static final long DefaultNumericId = 0;
		
		private long numericId = Long.MIN_VALUE;
		private String textId = null;
		private UniqueIdType idType = UniqueIdType.LongInt;
		private String imageDescription;
		
		public static ImageDescriptor create(String imageDescription, long id) {
			ImageDescriptor descriptor = new ImageDescriptor();
			descriptor.idType = UniqueIdType.LongInt;
			descriptor.numericId = id;
			descriptor.imageDescription = fixDescription(imageDescription);
			return descriptor;
		}

		public static ImageDescriptor create(String imageDescription, String id) {
			ImageDescriptor descriptor = new ImageDescriptor();
			descriptor.idType = UniqueIdType.String;
			descriptor.textId = id;
			descriptor.imageDescription = fixDescription(imageDescription);
			return descriptor;
		}
		
		/**
		 * In cases where the ImageDescriptor does not refer to an item that requires an id,
		 * set a default id so we pass validation.  This would be useful in a case that the 
		 * item the image describes will only have one single representation
		 * @param imageDescription
		 * @return
		 */
		public static ImageDescriptor create(String imageDescription) {
			ImageDescriptor descriptor = new ImageDescriptor();
			descriptor.idType = UniqueIdType.LongInt;
			descriptor.numericId = DefaultNumericId;
			descriptor.imageDescription = fixDescription(imageDescription);
			return descriptor;
		}
		
		/**
		 * Replace whitespace in image description with underscore for safer filename creation
		 * @param imageDescription
		 * @return
		 */
		private static String fixDescription(String imageDescription) {
			if(!StringUtils.isNullOrEmpty(imageDescription))
				imageDescription.replaceAll("\\s+", "_");
			return imageDescription;
		}

		public boolean isValid() {
			boolean isValid = true;
			isValid &= !StringUtils.isNullOrEmpty(imageDescription);
			switch(idType) {
			case LongInt:
				isValid &= numericId > Long.MIN_VALUE;
				break;
			case String:
				isValid &= !StringUtils.isNullOrEmpty(textId);
				break;
			}
			return isValid;
		}
		
		public String toString() {
			return String.format("%s_%s",
					imageDescription,
					idType == UniqueIdType.LongInt ? String.valueOf(numericId) : textId);
		}
	}
	
	private ImageDescriptor descriptor = null;
	private String url;
	private Extension ext = Extension.None;
	private Extension defaultExt = Extension.None;
	private int quality = DefaultQuality;
	
	public ImageInfo(ImageDescriptor descriptor, String url) {
		this.descriptor = descriptor;
		this.url = url;
		ext = getExtension(url);
	}
	
	/**
	 * Use the extra param defaultExt to have the toString method default filenames to a particular file type
	 * in the case that file type extension cannot be parsed from URL
	 * @param descriptor
	 * @param url
	 * @param defaultExt
	 */
	public ImageInfo(ImageDescriptor descriptor, String url, Extension defaultExt) {
		this.descriptor = descriptor;
		this.url = url;
		ext = getExtension(url);
		this.defaultExt = defaultExt;
	}
	
	/**
	 * Attempt to determine the file type of the image from the URL
	 * @param url
	 * @return
	 */
	private Extension getExtension(String url) {
		Extension extension = Extension.None;
		
		if(StringUtils.isValidUrl(url))
		{
			int start = url.lastIndexOf('.');
			int end = url.lastIndexOf('?');
			if(end < start)
				end = url.length();
			String ext = url.substring(start + 1, end);
			
			if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))
				extension = Extension.Jpg;
			else if(ext.equalsIgnoreCase("png"))
				extension = Extension.Png;
		}
		
		return extension;
	}

	@Override
	public String toString() {
		String extension = null;
		if(ext == Extension.None && defaultExt != Extension.None)
			extension = defaultExt.name().toLowerCase();
		else
			extension = ext.name().toLowerCase();
		
		return String.format("%s.%s",
				descriptor.toString(),
				extension);
	}
	
	public String getUrl() { return url; }
	public ImageDescriptor getDescriptor() { return descriptor; }
	public void setQuality(int quality) { this.quality = quality; }
	public int getQuality() { return quality; }

	public CompressFormat getCompressFormat() {
		CompressFormat format = CompressFormat.JPEG;
		if(ext == Extension.Png)
			format = CompressFormat.PNG;
		return format;
	}

}