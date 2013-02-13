package us.beacondigital.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


/**
 * ImageView that loads images from the web by caching them to disk
 * @author Rich
 *
 */
public class RemoteImageView extends LinearLayout {
	
	protected static boolean loggingEnabled = false;
	
	Context context;
	BitmapDrawable bitmapDrawable;
	ImageView imageView;
	ImageInfo imageInfo;
	
	boolean hasImage = false;
	boolean cacheToFile = true;
	
	DiskLoadTask diskLoadTask = null;
	RemoteLoadTask remoteLoadTask = null;
	
//	private final Handler handler = new Handler();
	
	/**
	 * Allows the consumer to declare a desired aspect ratio
	 * which we use to resize in onSizeChanged
	 * @author Rich
	 *
	 */
	public enum AspectRatio {
		Default (1, 1),
		Square (1, 1),
		Widescreen16x9 (16, 9),
		Widescreen4x3 (4, 3);
		
		private int width, height;
		private AspectRatio(int width, int height) {
			this.width = width;
			this.height = height;
		}
		public int getWidth() { return width; }
		public int getHeight() { return height; }
	}
	private AspectRatio aspectRatio = AspectRatio.Default;

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;		
		initImageView();
		initResizeLogic();
	}
	
	private void initResizeLogic() {
		ViewTreeObserver obs = getViewTreeObserver();
		obs.addOnPreDrawListener(new OnPreDrawListener() {
			
			public boolean onPreDraw() {
				if (aspectRatio != AspectRatio.Default && imageView.getWidth() > 0) {
					int height = imageView.getWidth() * aspectRatio.getHeight() / aspectRatio.getWidth();
					ViewGroup.LayoutParams lp = imageView.getLayoutParams();
					lp.height = height;
					imageView.setLayoutParams(lp);
				}
				getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});
	}

	public void setAspectRatio(AspectRatio aspectRatio) {
		this.aspectRatio = aspectRatio;
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
			hasImage = true;
			bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
			imageView.setImageDrawable(bitmapDrawable);
			setBackgroundResource(0);
		}
	}

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
		if(ImageInfoValidator.isValid(imageInfo))
		{
			if (diskLoadTask != null && !diskLoadTask.isCancelled()) {
				diskLoadTask.cancel(true);
			}
			diskLoadTask = new DiskLoadTask();
			diskLoadTask.execute(imageInfo);
			
		}
	}
	
	/**
	 * Local method for logging when statically enabled from application (off by default
	 * @param format
	 * @param params
	 */
	protected void log(String format, Object... params) {
		if(loggingEnabled) {
			Log.v(getClass().getSimpleName(), String.format(format, params));
		}
	}
	
	private class DiskLoadTask extends AsyncTask<ImageInfo, Void, Bitmap> {
		
		ImageInfo info = null;

		@Override
		protected Bitmap doInBackground(ImageInfo... params) {
			
			// attempt to load from local first
			info = params[0];
			ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
			Bitmap bitmap = imageCacheHelper.loadImage(info);
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(bitmap != null)
			{
				hasImage = true;
				bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
				imageView.setImageDrawable(bitmapDrawable);
			}
			else
			{
				if (remoteLoadTask != null && !remoteLoadTask.isCancelled()) {
					remoteLoadTask.cancel(true);
				}
				remoteLoadTask = new RemoteLoadTask();
				remoteLoadTask.execute(info);
			}
		}
		
	}
	
	private class RemoteLoadTask extends AsyncTask<ImageInfo, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(ImageInfo... params) {
			ImageInfo info = params[0];
			String url = info.getUrl();
			
			ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");				
			Bitmap bitmap = HttpHelper.getImage(url, client);
			client.close();
			if(bitmap != null)
			{
				// Save locally to SD
				if(cacheToFile) {
					imageCacheHelper.saveImage(bitmap, info);
				}
				hasImage = true;
			}
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			if (bitmap != null) {
				bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
				imageView.setImageDrawable(bitmapDrawable);
			}
		}
		
	}

}