package us.beacondigital.samples.activities;

import us.beacondigital.samples.R;
import us.beacondigital.utils.image.ImageInfo;
import us.beacondigital.utils.image.ImageInfo.ImageDescriptor;
import us.beacondigital.utils.image.RemoteImageView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity {

	private static final String IMAGE_DESC_RICH_AND_LUCAS = "Rich and Lucas";
	private static final String IMAGE_DESC_HARVARD_BRIDGE = "Harvard Bridge";
	
	private static final String URL_RICH_AND_LUCAS = "http://distilleryimage4.ak.instagram.com/5d616b6af10e11e29b6422000aa80460_7.jpg";
	private static final String URL_HARVARD_BRIDGE = "http://distilleryimage1.ak.instagram.com/ae2a2bd2c7c311e2a6b722000a1fc7c5_7.jpg";
	
	RemoteImageView img1 = null, img2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.main);
		
		initControls();
		initRemoteImages();
	}

	/**
	 * Initialize local Views
	 */
	private void initControls() {
		img1 = (RemoteImageView) findViewById(R.id.img1);
		img2 = (RemoteImageView) findViewById(R.id.img2);
	}
	
	/**
	 * Sample usage of the ImageInfo and ImageDescriptor classes to 
	 * load remote images from the web into a RemoteImageView with 
	 * caching both enabled and disabled
	 */
	private void initRemoteImages() {
		ImageInfo imgGithubInfo = new ImageInfo(ImageDescriptor.create(IMAGE_DESC_RICH_AND_LUCAS), URL_RICH_AND_LUCAS);
		img1.setImageInfo(imgGithubInfo);
		img1.setScaleType(ScaleType.CENTER_CROP);
		img1.setCacheToFile(false);
		img1.request();

		ImageInfo imgOctocatInfo = new ImageInfo(ImageDescriptor.create(IMAGE_DESC_HARVARD_BRIDGE), URL_HARVARD_BRIDGE);
		img2.setImageInfo(imgOctocatInfo);
		img2.setScaleType(ScaleType.CENTER_CROP);
		img2.request();
	}
}