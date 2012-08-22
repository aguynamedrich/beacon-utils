package us.beacondigital.android.samples.activities;

import us.beacondigital.android.samples.R;
import us.beacondigital.utils.ImageInfo;
import us.beacondigital.utils.ImageInfo.ImageDescriptor;
import us.beacondigital.utils.RemoteImageView;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static final String GithubLogo = "Github Logo";
	private static final String OctocatLogo = "Octocat Logo";
	
	private static final String OctocatLogoUrl = "https://a248.e.akamai.net/assets.github.com/images/modules/about_page/octocat.png";
	private static final String GithubLogoUrl = "https://a248.e.akamai.net/assets.github.com/images/modules/about_page/github_logo.png";
	
	RemoteImageView imgGithub = null, imgOctocat = null;
	
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
		imgGithub = (RemoteImageView) findViewById(R.id.imgGithub);
		imgOctocat = (RemoteImageView) findViewById(R.id.imgOctocat);
	}
	
	/**
	 * Sample usage of the ImageInfo and ImageDescriptor classes to 
	 * load remote images from the web into a RemoteImageView with 
	 * caching both enabled and disabled
	 */
	private void initRemoteImages() {
		ImageInfo imgGithubInfo = new ImageInfo(ImageDescriptor.create(GithubLogo), GithubLogoUrl);
		imgGithub.setImageInfo(imgGithubInfo);
		imgGithub.setCacheToFile(false);
		imgGithub.request();

		ImageInfo imgOctocatInfo = new ImageInfo(ImageDescriptor.create(OctocatLogo), OctocatLogoUrl);
		imgOctocat.setImageInfo(imgOctocatInfo);
		imgOctocat.request();
	}
}