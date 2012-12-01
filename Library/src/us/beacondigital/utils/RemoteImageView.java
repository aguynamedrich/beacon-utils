package us.beacondigital.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


/**
 * ImageView that loads images from the web by caching them to disk
 * @author Rich
 *
 */
public class RemoteImageView extends LinearLayout {
	
	private static boolean loggingEnabled = false;
	
	Context context;
	BitmapDrawable bitmapDrawable;
	ImageView imageView;
	ImageInfo imageInfo;
	
	boolean isAsync = false;
	boolean hasImage = false;
	boolean cacheToFile = true;
	
	private final Handler handler = new Handler();

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;		
		initImageView();
	}
	
	public static void setLoggingEnabled(boolean loggingEnabled) {
		RemoteImageView.loggingEnabled = loggingEnabled;
	}
	
	private void initImageView()
	{
		imageView = new ImageView(context);
		imageView.setLayoutParams(
				new LayoutParams(
						LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setAdjustViewBounds(true);
		addView(imageView);
	}
	
	@SuppressWarnings("deprecation")
	public void setImageBackground(Drawable background) {
		if(imageView != null && background != null) {
			// The following method isn't deprecated until Jelly Bean
			imageView.setBackgroundDrawable(background);
		}
	}
	
	public void setImageInfo(ImageInfo info)
	{
		if(imageInfo != null)
		{			
			// remove old image
			if(imageView.getDrawable() != null && imageView.getDrawable() == bitmapDrawable)
			{
				imageView.setImageDrawable(null);
				bitmapDrawable.getBitmap().recycle();
				bitmapDrawable = null;
			}
		}
			
		imageInfo = info;
	}
	
	/**
	 * Allow the user to bypass caching the image to disk.
	 * This defaults to true (caching to disk is turned on)
	 * @param shouldCacheImagesToFile
	 */
	public void setCacheToFile(boolean cacheToFile) {
		this.cacheToFile = cacheToFile;
	}
	
	/**
	 * Expose the image info publicly so an Activity can trigger a manual refresh
	 * when the download manager broadcasts download complete messages
	 * @return
	 */
	public ImageInfo getImageInfo() { return imageInfo; }
	
	/**
	 * Use this information to decide whether or not to trigger a refresh
	 * @return
	 */
	public boolean hasImage() { return hasImage; }
	
	/**
	 * For async images, manually refresh when image has been saved to disk
	 */
	public void refresh()
	{
		ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
		Bitmap bmp = imageCacheHelper.loadImage(imageInfo);
		if(bmp != null)
		{
			log("refresh: Image loaded from cache, %s", imageInfo.toString());
			log("cache dir: %s", imageCacheHelper.getCacheDirectory().getAbsolutePath());
			
			hasImage = true;
			bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
			imageView.setImageDrawable(bitmapDrawable);
			setBackgroundResource(0);
		}
	}

	public void setAsync(boolean async) { isAsync = async; }
	public void setScaleType(ScaleType scaleType)
	{
		imageView.setScaleType(scaleType);
	}
	public void setImageLayoutParams(int width, int height)
	{
		LayoutParams layoutParams = new LayoutParams(width, height);
		imageView.setLayoutParams(layoutParams);
	}
	
	/**
	 * public method for triggering image request
	 */
	public void request()
	{
		ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
		if(ImageInfoValidator.isValid(imageInfo))
		{
			// attempt to load from local first
			Bitmap bmp = imageCacheHelper.loadImage(imageInfo);
			if(bmp != null)
			{
				log("request: Image loaded from cache, %s", imageInfo.toString());
				log("cache dir: %s", imageCacheHelper.getCacheDirectory().getAbsolutePath());
				
				hasImage = true;
				bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
				imageView.setImageDrawable(bitmapDrawable);
			}
			else
			{
				if(isAsync)
				{
//					requestAsync();
				}
				else
				{
					requestInline();
				}
			}
			
		}
	}
	
	/**
	 * Request inline does it's own downloading and posts back to the UI thread when complete.
	 * This is used for images displayed on screens that won't be scrolling images off the screen so quickly
	 */
	private void requestInline()
	{
		final String url = imageInfo.getUrl();
		
		new Thread()
		{
			public void run()
			{
				ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");				
				Bitmap bmp = HttpHelper.getImage(url, client);
				client.close();
				if(bmp != null)
				{
					// Save locally to SD
					if(cacheToFile) {
						boolean saved = imageCacheHelper.saveImage(bmp, imageInfo);
						log("File saved: %b, %s", saved, imageInfo);
						log("cache dir: %s", imageCacheHelper.getCacheDirectory().getAbsolutePath());
					}
					hasImage = true;
					
					// Load bitmap for display on UI thread
					bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
					handler.post(new Runnable() {
						
						public void run() {
							log("requestInline: Image loaded from remote resource, %s", imageInfo.toString());
							imageView.setImageDrawable(bitmapDrawable);
						}
					});
				}
			}
		}.start();
	}
	
	/**
	 * Local method for logging when statically enabled from application (off by default
	 * @param format
	 * @param params
	 */
	private void log(String format, Object... params) {
		if(loggingEnabled) {
			Log.v(getClass().getSimpleName(), String.format(format, params));
		}
	}

}