# Go-Safe Mobile

The mobile app provides the necessary bridge between Aidevig device and the web server. Everything flows through the services provided by the mobile device thanks to Go-Safe App.

## Pre-requisites 

* Android SDK v23
* Latest Android Build Tools
* Android Support Repository

This use Gradle build system.
First download the code by cloning this repository or downloading an archived snapshot.

In Android Studio, use the **"Import non-Android Studio project"** or **"Import Project"** option. If prompted for a gradle configuration accept the default settings.
Built for mobiles with Android 5.0 (Lollipop) at least.

You will need to get a key for the Maps Android API in the google developer [console](https://console.developers.google.com).
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

You can add a string ressource, i.e google_maps_release_key used in AndroidManifest to call the ask for the Maps Android API.

## User Features 

* Displays a Map with Markers from other Aidevig devices
* Send to the server an alert launched by the Aidevig' Bracelet 
* Send automatic updates
* Manage personal profile
* Get the device (bracelet) information

## Dev Features 

Here are what the mobile is presently doing. For more details, you can get to the source code directly.
For more information, check the [activity lifecycle](http://developer.android.com/reference/android/app/Activity.html) and [fragment lifecycle](http://developer.android.com/guide/components/fragments.html).
This app uses volley to manage its HTTP services. Volley is an HTTP library that makes networking for Android apps easier and most importantly, faster. Volley is available through the open [AOSP](https://android.googlesource.com/platform/frameworks/volley) repository.

### Activity Package 

#### MainActivity

Main Activity that contains the menu and child fragments displayed in the contain_main.xml layout.
The MainActivity class extends AppCompatActivity to use the [support](http://developer.android.com/tools/support-library/index.html) library action features i.e here an action bar is used to call for the Navigation Menu.
This class implements several onFragmentInteractionListener that are called when a user clicks on an Menu Item.
It also instantiates a [GoogleApiClient](https://developers.google.com/android/guides/api-client) to use the Google Location features. The Location Services are also needed for the BluetoothManager class that handles the connectivity with an Aidevig wearable.

#### HomeFragment

This fragment displays a Google Map, with markers referencing alerts triggered from Aidevig bracelets. The layout is in the fragment_map.xml file.
Hence, the HomeFragment.class extends fragment and implements GoogleMap.onMapReadyCallback and GoogleAPI callbacks.
The [Google Map](https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap?hl=en) is instantiated in onCreateView method, because here the in the onCreate it is too soon to get the map instance.
Checkout the [Android Google Map API](https://developers.google.com/maps/documentation/android-api/) if you're not used to it.


#### ProfileFragment

Displays account user informations i.e 
* Complete Name
* Phone Number
* Mail Address

Probably should also display a profile picture.
Refer to the profile_fragment.xml to get the default layout.

The data should be fetched from the server when onCreate method is called.

#### ToolsFragment

Displays the device information i.e 

* Device Name
* MAC Address
* Software Version
* Last Synchronization Date

The data are fetched once the BluetoothManager is instantiated. 
Refer to the profile_fragment.xml to get the default layout.

#### StatsFragment

Diplays a list of different alert triggered by a the app user.

Still waiting for a service from the back returning the needed data.

### Connection Package 

#### AidVigeUrls

This contains the static routes to use the back services i.e getting the data to display on the map, posting alerts, etc.
It should be setted according to the server host.

#### SingleRequest

This Android App uses Volley to manage its HTTP requests. [Volley](http://developer.android.com/training/volley/index.html) excels at RPC-type operations used to populate a UI, such as fetching a page of search results as structured data. It integrates easily with any protocol and comes out of the box with support for raw strings, images, and JSON.

The Volley functionnality is implemented thanks to a [Singleton Pattern](http://developer.android.com/training/volley/requestqueue.html#singleton). A key concept is that the RequestQueue must be instantiated with the Application context, not an Activity context. This ensures that the RequestQueue will last for the lifetime of your app, instead of being recreated every time the activity is recreated (for example, when the user rotates the device). Once a RequestQueue instance is available, the following task is to create a Request (JSONObject Request or whatever) and then add it to the request queue.

#### BluetoothManager

Probably where all much of the communication logic is. The mobile and Bracelet devices are binded throught a Bluetooth standart protocol. 
The workflow is pretty much start a scan to find a device with the required UART profile and the right name, then provide a Bluetooth GATT functionnality through which the alerts are sent.
The mobile initiates the GATT commands and accept responses. The Bracelet is the device that receives GATT commands or requests and returns responses. We use the characteristic is a data value transferred between the client and the server. 
Workflow :

* [Setting](http://developer.android.com/guide/topics/connectivity/bluetooth.html#Permissions) up Bluetooth
* [Scanning](http://developer.android.com/guide/topics/connectivity/bluetooth.html#FindingDevices) for other Bluetooth devices
* Setting a [Bluetooth GATT](http://toastdroid.com/2014/09/22/android-bluetooth-low-energy-tutorial/) functionnality:
  * Discover all the UUIDs for all the primary services
  * Read and write descriptors for a particular characteristic, this is how messages are sent between the mobile and the device
* Managing the connectivity state

See this sample to get knowledge of other working examples [ex1](https://github.com/googlesamples/android-BluetoothLeGatt), [ex2](https://android.googlesource.com/platform/development/+/cefd49aae65dc85161d08419494071d74ffb982f/samples/BluetoothLeGatt/src/com/example/bluetooth/le/BluetoothLeService.java)

## Directory Structure

```
    |-- libs                    # dependencies used to build debug variants
    |-- build                   # script collection
    |-- build.gradle            # main mobile app build script
    |-- google-services.json    # google-services files configured via google developper console
    |-- mobile.iml              # 
    |-- mobile-debug.apk        # .apk file mobile debug release
    |-- mobile-release.apk      # .apk file mobile app release
    |-- proguard-rules.pro      # specific ProGuard rules
    |-- wear-release.apk        # .apk file wear app release
    `-- src                     # test assets, resources and code
        |-- build.gradle        # main mobile app build script
        |-- androidTest         # main java test source code
        |-- debug               # debug files
        |-- release             # release files present in distributed version
        |-- test                # test result files
        `-- main                # test assets, resources and code
            |-- java                 # main project java code
            |-- res                  # android resources files
            `-- AndroidManifest.xml  # essential information about the app provided to the Android system
```

## License

• [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

