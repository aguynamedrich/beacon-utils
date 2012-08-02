package us.beacondigital.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 
 * @author Rich
 *
 */
public class ImageFileHelper {
	
	private boolean isInitialized = false;
	File imagesDir = null;
	
	/**
	 * 
	 * @param root
	 */
	public void init(String root, String subDir) {
		File rootDir = new File(Environment.getExternalStorageDirectory(), root);
		if(!StringUtils.isNullOrEmpty(subDir)) {
			imagesDir = new File(rootDir, subDir);
		}
		else {
			imagesDir = rootDir;
		}
		imagesDir.mkdirs();
		isInitialized = true;
	}
	
	public boolean saveImage(Bitmap bitmap, ImageInfo info) {
		boolean success = false;
		FileOutputStream fos = null;

		if(isInitialized) {
			try
			{
				imagesDir.mkdirs(); // Juuuuuust in case
				File imageFile = new File(imagesDir, info.toString());
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
				imagesDir.mkdirs(); // Juuuuuust in case
				File imageFile = new File(imagesDir, info.toString());
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

}
