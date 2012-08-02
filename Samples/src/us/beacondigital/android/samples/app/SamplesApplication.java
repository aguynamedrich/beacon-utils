package us.beacondigital.android.samples.app;

import us.beacondigital.android.utils.ServiceLocator;
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
	}

}
