## Android Clustering library for [Yandex MapKit](https://tech.yandex.ru/maps/doc/mapkit/3.x/concepts/android/quickstart-docpage/)
### Features:
 - Clustering
 - Animations
 - Based on Google's [android-map-utils](https://github.com/googlemaps/android-maps-utils) [algorithms](https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/clustering/algo)
 - Can be used to implement clustering for any other map library
 
<div style="text-align:center"><img src="/images/demo.gif"/></div>

___

### Usage:
Please check out [sample app](sample)

or

Add it in your root build.gradle at the end of repositories:

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.65apps:android-clustering-for-yandex-mapkit:v0.2'
	}
___
 
 ### License:
 [Apache License 2.0](LICENSE)