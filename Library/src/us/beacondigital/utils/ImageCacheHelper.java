package us.beacondigital.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

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
	
	public File getCacheDirectory() { return cacheDirectory; }

}
