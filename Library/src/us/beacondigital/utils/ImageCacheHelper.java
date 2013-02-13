package us.beacondigital.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Class used by the RemoteImageView and external image downloading mechanisms to save remote images to 
 * disk and to load images from disk.
 * @author Rich
 *
 */
public class ImageCacheHelper {
	
	public static String DefaultRootDirectory = "beacon_digital";
	
	public enum StorageLocation {
		ApplicationCache,
		ExternalStorage
	}
	
	private StorageLocation storageLocation = StorageLocation.ExternalStorage;
	private boolean isInitialized = false;
	File cacheDirectory = null;
	String rootPath = DefaultRootDirectory;
	String imagesPath = null;
	MemCache memCache = null;
	
	/**
	 * 
	 * @param rootPath
	 * @param imagesPath
	 * @param memCacheSize Percentage of total memory class from the ActivityManager that should be used for remote image caching.
	 * Should be between 0.0 and 0.5 or we will throw an exception
	 */
	public void init(String rootPath, String imagesPath, float memCacheSize) {
		if (memCacheSize < 0f || memCacheSize > 0.5f)
			throw new IllegalArgumentException("Cache size should be a positive float no greater than 0.5");
		
		init(rootPath, imagesPath);
		ActivityManager am = (ActivityManager) ServiceLocator.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
		int memClassBytes = am.getMemoryClass() * 1024 * 1024;
		memCache = new MemCache((int) (memClassBytes * memCacheSize));
	}
	
	/**
	 * 
	 * @param root
	 */
	public void init(String rootPath, String imagesPath) {
		
		this.rootPath = rootPath;
		this.imagesPath = imagesPath;

		File storage = null;
		switch(storageLocation) {
		case ApplicationCache:
			storage = ServiceLocator.getAppContext().getCacheDir();
			break;
		case ExternalStorage:
			storage = Environment.getExternalStorageDirectory();
			break;
		}
		
		File rootDir = new File(storage, rootPath);
		if(!StringUtils.isNullOrEmpty(imagesPath)) {
			cacheDirectory = new File(rootDir, imagesPath);
		}
		else {
			cacheDirectory = rootDir;
		}
		cacheDirectory.mkdirs();
		isInitialized = true;
	}
	
	public void setStorageLocation(StorageLocation location) {
		storageLocation = location;
		init(rootPath, imagesPath);
	}
	
	public boolean saveImage(Bitmap bitmap, ImageInfo info) {
		if (memCache != null)
			memCache.put(info.toString(), bitmap);
		
		boolean success = false;
		FileOutputStream fos = null;

		if(isInitialized) {
			try
			{
				cacheDirectory.mkdirs(); // Juuuuuust in case
				File imageFile = new File(cacheDirectory, info.toString());
				fos = new FileOutputStream(imageFile);
				success = bitmap.compress(info.getCompressFormat(), info.getQuality(), fos);
			}
			catch(NullPointerException ex) {}
			catch(IOException ex) {}
			finally {
				IOUtils.safeClose(fos);
			}
		}
		
		return success;
	}
	
	public Bitmap loadImage(ImageInfo info) {
		
		FileInputStream fis = null;
		Bitmap bitmap = null;

		if (memCache != null) {
			bitmap = memCache.get(info.toString());
			if (bitmap != null) {
				log("memory cache hit for " + info.toString());
				return bitmap;
			}
		}
		
		if(isInitialized) {
			try
			{
				cacheDirectory.mkdirs(); // Juuuuuust in case
				File imageFile = new File(cacheDirectory, info.toString());
				fis = new FileInputStream(imageFile);
				bitmap = BitmapFactory.decodeStream(fis);
			}
			catch(NullPointerException ex) {}
			catch (FileNotFoundException e) { }
			finally {
				IOUtils.safeClose(fis);
			}
		}
		
		return bitmap;
	}
	
	public void flushCache() {
		if (cacheDirectory != null && cacheDirectory.isDirectory()) {
			File[] files = cacheDirectory.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					boolean deleted = file.delete();
					Log.d(
							ImageCacheHelper.class.getSimpleName(),
							String.format("flushCache::deleting %s. succeeded:%b", file.getName(), deleted));
				}
			}
		}
	}
	
	public File getCacheDirectory() { return cacheDirectory; }
	
	private class MemCache extends LruCache<String, Bitmap> {

		public MemCache(int maxSizeBytes) {
			super(maxSizeBytes);
		}
	}
	
	/**
	 * Local method for logging when statically enabled from application (off by default
	 * @param format
	 * @param params
	 */
	private void log(String format, Object... params) {
		if(RemoteImageView.loggingEnabled) {
			Log.v(getClass().getSimpleName(), String.format(format, params));
		}
	}

}
