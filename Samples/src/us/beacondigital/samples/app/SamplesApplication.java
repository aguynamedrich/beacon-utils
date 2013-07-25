package us.beacondigital.samples.app;

import us.beacondigital.samples.R;
import us.beacondigital.utils.ServiceLocator;
import us.beacondigital.utils.image.ImageCacheHelper;
import us.beacondigital.utils.image.ImageCacheHelper.StorageLocation;
import us.beacondigital.utils.image.RemoteImageView;
import android.app.Application;

public class SamplesApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		/**
		 * Always initialize the ServiceLocator with a context from your application.
		 * The main purpose for doing so is that helper/utility classes in your application
		 * can use application features (such as resources) without needing a connection
		 * to the activities, services and dialogs that use them
		 */
		ServiceLocator.init(this);
		
		/**
		 * Initialize the ImageFileHelper so that images loaded by RemoteImageView controls 
		 * are saved to a common directory within this application.  The path will be
		 * [External storage root]/[app directory]/[images directory].
		 * In this case, [app directory] is a descriptive name for the application and
		 * [images directory] is ".images".  Place the dot (.) in front of the folder name will 
		 * in theory hide these images from the user's gallery application(s).  If this is not your 
		 * desired scenario, use a directory name that does not start with a dot.
		 */
		ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
		imageCacheHelper.setStorageLocation(StorageLocation.ExternalStorage);
		imageCacheHelper.init(getString(R.string.app_directory), getString(R.string.images_directory));
		
		RemoteImageView.setLoggingEnabled(true);
	}

	/**
	 * Easy wrapper around ServiceLocator getApp/getAppContext functionality
	 * to get this instance statically throughout the app
	 * @return
	 */
	public static SamplesApplication get() {
		return ServiceLocator.getApp(SamplesApplication.class);
	}
}
