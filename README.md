beacon-utils
============

Beacon Utils is a collection of common utilities that make developing Android apps easier.

## ServiceLocator

ServiceLocator is a dependency container of sorts that you can use to preload, lazy load and discover utility and helper classes that are used throughout your application.  ServiceLocator removes the need for the various classes in your application to be aware of each other and allows classes without a hierarchical relationship to share common functionality without maintaining references to one another.  ServiceLocator also supports declaring interface implementations at runtime so you can program to an interface in cases where you may need to swap implementations based on your development cycle or deployment target.

## RemoteImageView

RemoteImageView allows you to easily load images from web url's without having to do the extra work of downloading and caching the files.  There are several classes that compliment RemoteImageView that make it easy to set up your caching layer and describing the images to avoid having name clashes of your files in cache.

## UrlHelper

UrlHelper contains a collection of convenience methods for quickly and easily retrieving data from the web.  Currently, most HTTP verbs are supported and there are overloaded methods for getting a String, Bitmap, HttpResponse or just the headers from a resource on the web.

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