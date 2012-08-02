package us.beacondigital.android.utils;

import android.graphics.Bitmap.CompressFormat;

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
		
		private long numericId = Long.MIN_VALUE;
		private String textId = null;
		private UniqueIdType idType = UniqueIdType.LongInt;
		private String imageDescription;
		
		public static ImageDescriptor create(long id, String imageDescription) {
			ImageDescriptor descriptor = new ImageDescriptor();
			descriptor.idType = UniqueIdType.LongInt;
			descriptor.numericId = id;
			descriptor.imageDescription = imageDescription;
			return descriptor;
		}
		
		public static ImageDescriptor create(String id, String imageDescription) {
			ImageDescriptor descriptor = new ImageDescriptor();
			descriptor.idType = UniqueIdType.String;
			descriptor.textId = id;
			descriptor.imageDescription = imageDescription;
			return descriptor;
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
	
	private ImageDescriptor descriptor;
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