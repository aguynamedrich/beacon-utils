Beacon Utils for Android
============

Beacon Utils is a collection of common utilities that make developing Android apps easier.

## ServiceLocator

ServiceLocator is a dependency container of sorts that you can use to preload, lazy load and discover utility and helper classes that are used throughout your application.  ServiceLocator removes the need for the various classes in your application to be aware of each other and allows classes without a hierarchical relationship to share common functionality without maintaining references to one another.  ServiceLocator also supports declaring interface implementations at runtime so you can program to an interface in cases where you may need to swap implementations based on your development cycle or deployment target.

### To initialize ServiceLocator
Pass an instance of your Application to ServiceLocator when app is launched.  The example below is an example of doing this from a custom Application class.
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.yourdomain.application"
    android:versionCode="1"
    android:versionName="1.0"
    android:installLocation="auto">

    <application
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:name=".MyApplication">
```
```java
public class MyApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();        
    	ServiceLocator.init(this);
``` 
Or, if you're not using a custom Application subclass, you can initialize ServiceLocator from within your MainActivity
```java
public class MainActivity extends Activity {
	
	@Override
	public void onCreate() {
		super.onCreate();
    	ServiceLocator.init(getApplicationContext());
```

### To use ServiceLocator as a dependency container
```java
		
// To register a concrete implementation on an interface...
ServiceLocator.register(IDataProvider.class, MockDataProvider.class);

// To resolve a concrete implementation for the interface you need...
IDataProvider dataProvider = ServiceLocator.resolve(IDataProvider.class);
// And now you can use your object...
DataObject obj = dataProvider.getDataObject();

// To resolve a lazy-loaded singleton object...
ServiceDataCache serviceDataCache = ServiceLocator.resolve(ServiceDataCache.class);
```

### To use ServiceLocator to retrieve your Application context from anywhere in your app
If you need a reference to your current Application context from within an Activity, you can simply call getAppliationContext().
However, you may need a reference to your Application context from custom classes that you've created that don't inherit this method from the Context superclass.
For example, you may need access to your application's resources and assets from inside of a custom helper class, or you may need to access your application's private files or cache directory.
Here are examples of both cases.
```java
// If you just need access to methods on the Application class...
Application app = ServiceLocator.getAppContext();
Resources res = app.getResources();
File filesDir = app.getFilesDir();
File cacheDir = app.getCacheDir();

// But if you need specific methods on your custom application class...
MyApplication app = ServiceLocator.getApp(MyApplication.class);
app.customMethod();
```

## RemoteImageView

RemoteImageView allows you to easily load images from web url's without having to do the extra work of downloading and caching the files.  There are several classes that compliment RemoteImageView that make it easy to set up your caching layer and describing the images to avoid having name clashes of your files in cache.

### Initial setup for cache and logging preferences
These settings allow you to control where the downloaded files are cached as well as to turn logging on and off.  I typically run this setup once in the Application class on first launch (onCreate).  The init method takes an optional float to turn on in-memory caching.  This will speed up the loading time of your images and is particularly useful for thumbnails in a ListView or GridView when scrolling.  The parameter used calculates how much memory to use for the cache as a percentage of the estimated memory space for your app as determined by the ActivityManager.  In the example below, 15% of the total memory space will be used for caching remote images.
```java
    	
// Use 15% of estimated application memory space for remote image memory cache
float lruCacheSize = 0.15f;
ImageCacheHelper imageCacheHelper = ServiceLocator.resolve(ImageCacheHelper.class);
imageCacheHelper.init(ImageCacheDir, ImageCacheSubDir, lruCacheSize);
imageCacheHelper.setStorageLocation(StorageLocation.ApplicationCache);
imageCacheHelper.flushCache();
RemoteImageView.setLoggingEnabled(true);
```
### Resolve RemoteImageView items in your Activity, initialize your ImageInfo objects, and trigger async download and UI refresh
The ImageInfo and ImageDescriptor classes are used to build a unique cache key to avoid name clashes.  You can also bypass caching images to file so you can control which images are refreshed on every request.
```java		
userThumbnail = (RemoteImageView) findViewById(R.id.userThumbnail);
projectImage = (RemoteImageView) findViewById(R.id.projectImage);

// The 'user' and 'project' objects used in the example are assumed to be
// some model objects that the RemoteImageView will be associated to.

ImageDescriptor descriptor = ImageDescriptor.create(user.id, "User", user.name);
ImageInfo imageInfo = new ImageInfo(descriptor, user.getThumbnailUrl());
userThumbnail.setImageInfo(imageInfo);
userThumbnail.setCacheToFile(false);
userThumbnail.setScaleType(ScaleType.CENTER_CROP);
userThumbnail.request();

descriptor = ImageDescriptor.create(project.id, "Project", project.title);
imageInfo = new ImageInfo(descriptor, project.getImageUrl());
projectImage.setImageInfo(imageInfo);
projectImage.setScaleType(ScaleType.FIT_CENTER);
projectImage.request();
```

You can also add a GestureDetector to the RemoteImageView to process touch events and gestures.  Here is an example of adding a double tap gesture listener to a RemoteImageView within a fragment (notice that the GestureDetector constructor requires an activity context reference.  From within your Activity, you can pass "this").

```java
image.setGestureDetector(new GestureDetector(getActivity(), new DoubleTapListener()));

private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
	
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// Process double tap here...
		return true;
	}
}
```

## HttpHelper

HttpHelper contains a collection of convenience methods for quickly and easily retrieving data from the web.  Currently, most HTTP verbs are supported and there are overloaded methods for getting a String, Bitmap, HttpResponse or just the headers from a resource on the web.

```java
// To load an image from a remote URL...
String imageUrl = "https://a248.e.akamai.net/assets.github.com/images/modules/about_page/octocat.png";
Bitmap bitmap = HttpHelper.getImage(imageUrl);

// To load the contents of a web page...
String webPageUrl = "http://www.google.com";
String webPageBody = HttpHelper.read(webPageUrl);

// To load the contents of an api endpoint...
String jsonDataUrl = "http://en.gravatar.com/aguynamedrich.json";
String jsonData = HttpHelper.read(jsonDataUrl);

// To get an HttpResponse from a url, check the status code, and read the response
// using a combination of HttpClientProvider, StringUtils, IOUtils and HttpHelper
String url = "http://www.google.com";
HttpClient client = HttpClientProvider.get();
HttpResponse response = HttpHelper.getResponse(url, client);
if(HttpUtils.isOK(response)) {
	String body = StringUtils.readStream(response);
	processResult(body);
}
else {
	processError(response.getStatusLine().getStatusCode());
}
IOUtils.safeClose(client);
```

## UrlHelper

UrlHelper contains a number of overloaded methods to easily add query string parameters of any basic Java value type to a url with a single call.

## StringUtils, IOUtils, HttpUtils, JSONHelper, Encrypt, UrlParamEncoder

These classes contain utility methods for common tasks that you are likely to encounter during Android development.  Most functionality is exposed as single static method calls for the purpose of making your code easier to read, write and understand.

```java
// To find out if a String contains enough data to work with...
boolean empty = StringUtils.isNullOrEmpty(data);

// To read the body of an HttpResponse into a String...
String body = StringUtils.readStream(response);

// To read an InputStream into a String...
String content = StringUtils.readStream(inputStream);

// To make sure a Cursor object is closed without the need to check for null or handle an exception...
IOUtils.safeClose(cursor);

// To determine if the status code of an HttpResponse is in the 200 range...
HttpUtils.isOK(response);

// To determine if the status code of an HttpResponse is in the 400 range...
HttpUtils.isUnauthorized(response);
```