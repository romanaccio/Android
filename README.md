# Android Devices

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

## Contributing 

If you want to get involved in the project, send a mail to aidevig@gmail.com
Checkout the README.md in the mobile folder

You will need to get a key for the Maps Android API used in the app,  via the google developer [console](https://console.developers.google.com).

In other to do so : 

* Go the Google Maps Android API [here](https://developers.google.com/maps/documentation/android-api/) and get the key
* Follow the instruction
* Once they ask you to put the SHA1 fingerprint of your app, get the **release SHA1** in your android studio : 
  * go to build -> generate signed APK
  * Create or use existing keystore.
  * Open terminal in android studio and type
  ````
  keytool -list -v -keystore <your_keystore_name> -alias <your_alias_name>
  ````
  Where : <your_keystore_name> is the path and name of the keystore, including the .keystore extension for example : Users/Projects/AndroidStudioProjects/Android/keystorename.jks
          <your_alias_name> is alias that you assigned to the certificate when you created it.
  If prompted to type the password, then you should get your SHA1 fingerprint.

Checkout this [tutorial](http://android-er.blogspot.in/2012/12/displaying-sha1-certificate-fingerprint.html) about it.

