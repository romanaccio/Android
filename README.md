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
Follow these [instructions](https://developers.google.com/maps/documentation/android-api/signup)
  
You can check in File -> Project Structure if the the build types are well set.

Checkout this [tutorial](http://android-er.blogspot.in/2012/12/displaying-sha1-certificate-fingerprint.html) about it.

You can add a string ressource, i.e google_maps_release_key used in AndroidManifest to call the ask for the Maps Android API.

## License

* [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

