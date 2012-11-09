beacon-utils
============

Beacon Utils is a collection of common utilities that make developing Android apps easier.

## ServiceLocator

ServiceLocator is a dependency container of sorts that you can use to preload, lazy load and discover utility and helper classes that are used throughout your application.  ServiceLocator removes the need for the various classes in your application to be aware of each other and allows classes without a hierarchical relationship to share common functionality without maintaining references to one another.

## RemoteImageView

RemoteImageView allows you to easily load images from web url's without having to do the extra work of downloading and caching the files.  There are several classes that compliment RemoteImageView that make it easy to set up your caching layer and describing the images to avoid having name clashes of your files in cache.