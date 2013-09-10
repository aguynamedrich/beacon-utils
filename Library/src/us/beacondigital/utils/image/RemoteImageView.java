package us.beacondigital.utils.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import us.beacondigital.utils.IOUtils;
import us.beacondigital.utils.ServiceLocator;
import us.beacondigital.utils.net.HttpClientProvider;
import us.beacondigital.utils.net.WebRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
	
	static int maxDownloads = 0;
	static Semaphore semaphore = null;
	DiskLoadTask diskLoadTask = null;
	RemoteLoadTask remoteLoadTask = null;
	
	GestureDetector gestureDetector = null;
	
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
	
	public enum ResizeAnchor {
		Width,
		Height
	}
	
	private AspectRatio aspectRatio = AspectRatio.Default;
	private ResizeAnchor resizeAnchor = ResizeAnchor.Width;

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
				applyAspectRatio();
				getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});
	}
	
	/**
	 * Cancel any pending tasks when view is detached
	 */
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		cancel();
		
	}

	public synchronized void applyAspectRatio() {
		if (aspectRatio != AspectRatio.Default && imageView.getWidth() > 0) {
			ViewGroup.LayoutParams lp = imageView.getLayoutParams();
			if (resizeAnchor == ResizeAnchor.Width) {
				int height = imageView.getWidth() * aspectRatio.getHeight() / aspectRatio.getWidth();
				lp.height = height;
			}
			else {
				int width = imageView.getHeight() * aspectRatio.getWidth() / aspectRatio.getHeight();
				lp.width = width;
			}
			imageView.setLayoutParams(lp);
		}
	}

	public void setAspectRatio(AspectRatio aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	
	public static void setLoggingEnabled(boolean loggingEnabled) {
		RemoteImageView.loggingEnabled = loggingEnabled;
	}
	
	/**
	 * Allows the application to set a maximum number of simultaneous downloads
	 * and disk reads
	 * @param max
	 */
	public static void setMaxDownloads(int max) {
		if (max > 0) {
			maxDownloads = max;
			semaphore = new Semaphore(maxDownloads, true);
		}
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
	
	public void setBackgroundResource(int resid) {
		if (imageView != null) {
			imageView.setBackgroundResource(resid);
		}
	}
	
	public void setImageResource(int resId) {
		if (imageView != null) {
			imageView.setImageResource(resId);
		}
	}
	
	public void setBackgroundColor(int color) {
		if (imageView != null) {
			imageView.setBackgroundColor(color);
		}
	}
	
	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector != null)
			return gestureDetector.onTouchEvent(event);
		else
			return super.onTouchEvent(event);
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
			refresh(bmp);
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

			ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
			Bitmap bitmap = imageCacheHelper.loadFromCache(imageInfo);
			
			if (bitmap != null) {
				refresh(bitmap);
			}
			else {
				diskLoadTask = new DiskLoadTask();
				diskLoadTask.execute(imageInfo);
			}
		}
	}
	
	/**
	 * Exposes the ability to cancel the operation.
	 * A good example usage of this is when an image is requested and the enclosing
	 * Activity or Fragment is destroyed before the operation is finished
	 */
	public void cancel() {
		log("calling cancel");
		if (remoteLoadTask != null && !remoteLoadTask.isCancelled()) {
			remoteLoadTask.cancel(true);
		}
		if (diskLoadTask != null && !diskLoadTask.isCancelled()) {
			diskLoadTask.cancel(true);
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
			acquireSemaphorePermit();
			
			// attempt to load from local first
			info = params[0];
			ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
			Bitmap bitmap = imageCacheHelper.loadImage(info);			
			return bitmap;
		}
		
		@Override
		protected void onCancelled(Bitmap result) {
			releaseSemaphorePermit();
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(bitmap != null)
			{
				if (info != null && info.equals(imageInfo)) {
					hasImage = true;
					refresh(bitmap);
				}
				releaseSemaphorePermit();
			}
			else
			{
				if (remoteLoadTask != null && !remoteLoadTask.isCancelled()) {
					remoteLoadTask.cancel(true);
				}
				releaseSemaphorePermit();
				remoteLoadTask = new RemoteLoadTask();
				remoteLoadTask.execute(info);
			}
		}
		
	}
	
	private class RemoteLoadTask extends AsyncTask<ImageInfo, Void, Bitmap> {
		
		ImageInfo info = null;
		String url = null;
		DefaultHttpClient client = HttpClientProvider.get();
		InputStream stream = null;

		@Override
		protected Bitmap doInBackground(ImageInfo... params) {
			acquireSemaphorePermit();
			
			info = params[0];
			url = info.getUrl();
			
			ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
			Bitmap bitmap = null;
			
			try {
				log("Begin download: %s", url);
				HttpResponse response = WebRequest.execute(client, url);
				stream = response.getEntity().getContent();
				bitmap = BitmapFactory.decodeStream(stream);
			}
			catch (IllegalStateException e) {
				log("RemoteLoadTask:%s caught while downloading: %s", e.getClass().getSimpleName(), url);
			}
			catch (IOException e) {
				log("RemoteLoadTask:%s caught while downloading: %s", e.getClass().getSimpleName(), url);
			}
			catch (NullPointerException e) {
				log("RemoteLoadTask:%s caught while downloading: %s", e.getClass().getSimpleName(), url);
			}
			catch (OutOfMemoryError e) {
				log("RemoteLoadTask:%s caught while downloading: %s", e.getClass().getSimpleName(), url);
				log("Running GC and trimming memory cache");
				System.gc();
				if (imageCacheHelper == null) {
					imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
				}
				imageCacheHelper.trimMemoryCache();
				
			}
			finally {
				log("Closing stream for url: %s", url);
				IOUtils.safeClose(stream);
				IOUtils.safeClose(client);
			}
			
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
		protected void onCancelled(Bitmap result) {
			log("onCancelled for url: %s", url);
			releaseSemaphorePermit();
		}
		
		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			if (bitmap != null) {
				if (info != null && info.equals(imageInfo)) {
					refresh(bitmap);
				}
			}
			releaseSemaphorePermit();
		}
		
	}

	public void refresh(Bitmap bitmap) {
		setBackgroundResource(0);
		bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
		imageView.setImageDrawable(bitmapDrawable);
		applyAspectRatio();
	}

	/**
	 * Acquire a semaphore permit.  Includes logging and exception swallowing
	 */
	public void acquireSemaphorePermit() {
		try {
			if (semaphore != null) {
				long threadId = Thread.currentThread().getId();
				log("[%d] Acquiring permit, size:%d, avail:%d", threadId, maxDownloads, semaphore.availablePermits());
				semaphore.acquire();
				log("[%d] Permit acquired", threadId);
			}
		}
		catch (Exception ex) { }
	}

	/**
	 * Releasing a semaphore permit.  Includes logging and exception swallowing
	 */
	public void releaseSemaphorePermit() {
		try {
			if (semaphore != null) {
				long threadId = Thread.currentThread().getId();
				log("[%d] Releasing permit, size:%d, avail:%d", threadId, maxDownloads, semaphore.availablePermits());
				semaphore.release();
				log("[%d] Permit released", threadId);
			}
		}
		catch (Exception ex) { }
	}

}