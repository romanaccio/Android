# Android Prototype

This is the repo containing source code of the Android Mobile. Check out the mobile folder to get the source code of the mobile app.

The Wear app is just in the getting-started-stage, so if there is plenty room for improvment. 

## Pre-requisites 

* Android SDK v23
* Latest Android Build Tools
* Android Support Repository

These applications use Gradle build system.
First download the code by cloning this repository or downloading an archived snapshot.

In Android Studio, use the **"Import non-Android Studio project"** or "Import Project" option. If prompted for a gradle configuration accept the default settings.
Don't forget to set the path of the keystore. The default path should change but the file must be the same

## Aidevig App

The Aidevig app requires an aidevig bracelet built with a bluetooth module to communicate with the app. 
A User can get access to a map to see alert triggered by different users. He can also trigger alerts, by establishing a bluetooth connection between the mobile and the bracelet.
The bracelet can monitor the pulse, detects souncs, all data which are then combined with other factors i.e location, hours, level to analyse and filter the alerts.

### User Features 

* Displays a Map with Markers from other Aidevig devices
* Send to the server an alert launched manually or automatically by the Aidevig' Bracelet 
* Send automatic updates
* Manage personal profile
* Get the device (bracelet) information

## Contributing 

If you want to get involved in the project, send a mail to aidevig@gmail.com.

Checkout the README.md in the mobile folder as it more concise.

You will need to get a key for the Maps Android API used in the app,  via the google developer [console](https://console.developers.google.com).

In other to do so : 

* Go the Google Maps Android API [here](https://developers.google.com/maps/documentation/android-api/) and get the key
* Follow the instruction
* Once they ask you to put the [SHA1](https://en.wikipedia.org/wiki/SHA-1) fingerprint of your app, get the **release SHA1** in your android studio : 
  * go to build -> generate signed APK
  * Create or use existing keystore.
  * Open terminal in android studio and type
  ````
  keytool -list -v -keystore your_keystore_name -alias your_alias_name
  ````
  Where : __your_keystore_name__ is the path and name of the keystore, including the .keystore extension for example : Users/Projects/AndroidStudioProjects/Android/keystorename.jks
          __your_alias_name__ is alias that you assigned to the certificate when you created it.
  If prompted to type the password, then you should get your SHA1 fingerprints.
  

You can check in File -> Project Structure if the Signing Config and the build types are well set.

Checkout this [tutorial](http://android-er.blogspot.in/2012/12/displaying-sha1-certificate-fingerprint.html) about it.

## License

* [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

