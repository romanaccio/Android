/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.dty.gosafe.connection;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dty.gosafe.activity.MainActivity;
import com.dty.gosafe.activity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Dasha on 14/01/2016.
 */

/**
 * Bluetooth Connectivity Manager to the Aidevig device manager
 */
public class BluetoothManager {

    /**
     * UUIDs for UAT service and associated characteristics.
     */
    public static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static int REQUEST_ENABLE_BT = 1404;

    private static BluetoothManager mInstance;
    /**
     *  Location sent with every alert triggered by the Aidevig device
     */

    /**
     * Represents the local device Bluetooth adapter. The BluetoothAdapter lets you perform fundamental Bluetooth tasks, such as initiate device discovery, query a list of bonded (paired) devices, instantiate a BluetoothDevice using a known MAC address, and create a BluetoothServerSocket to listen for connection requests from other devices, and start a scan for Bluetooth LE devices.
     */
    private BluetoothAdapter adapter;
    private BluetoothGatt gatt;

    /**
     * Set to true when device is found
     */
    public boolean isDeviceFound = false;

    /**
     * Represents a remote Bluetooth device. A BluetoothDevice lets you create a connection with the respective device or query information about it, such as the name, address, class, and bonding state.
     */
    private BluetoothDevice deviceDetected;

    /**
     * BluetoothCharacteristic used to communicate to the smart object.
     * Represents a Bluetooth GATT Characteristic
     * A GATT characteristic is a basic data element used to construct a GATT service, BluetoothGattService.
     * The characteristic contains a value as well as additional information and optional GATT descriptors, BluetoothGattDescriptor.
     */
    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;

    /**
     *
     */
    private ScanCallback scanCallback;

    /**
     * The activity to which the BA is binded
     */
    private Activity activity;

    /**
     * Location sent with the BraceletData to the WebServer
     */
    private Location mLastLocation;

    /**
     * Instance of a SingleRequest used to send data to the WebServer
     */
    private SingleRequest postRequests;
    private int mId = 1201;


    public BluetoothDevice getDeviceDetected() {
        return deviceDetected;
    }

    public void setLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
        sendPosts("A00");
    }


    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    // BTLE state

    private BluetoothManager(Activity activity){
        this.activity = activity;

        this.enableBluetooth();

        this.adapter = BluetoothAdapter.getDefaultAdapter();

        this.initScanCallback();
        postRequests = SingleRequest.getInstance(activity, AidVigeUrls.ALERT);

        // Create an instance of GoogleAPIClient.
    }

    public static synchronized BluetoothManager getInstance(Activity activity){
        if(mInstance ==null){
            mInstance = new BluetoothManager(activity);
        }
        return mInstance;
    }

    /**
     * Build notifications, for Bluetooth connection status and alert sent
     * @param title : Title of the notification
     * @param alert : Message to show in the notification bar
     */
    public void createNotification(String title, String alert){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.mipmap.ic_aidge)
                        .setContentTitle(title)
                        .setContentText(alert);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(activity, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void enableBluetooth(){
        if(this.adapter ==null){
            writeLine("Activate Bluetooth");
        }else{
            if(!this.adapter.isEnabled()){
                checkBluetoothConnection();
                this.adapter.enable();
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    private void writeLine(String text) {
        Log.d("Bluetooth Stage", text);
    }

    private void sendNotification(String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    /*public void sendClick(View view) {
        confirmReception();
    }*/

    /**
     * Send a message to the device to confirm the reception of the sent data
     */
    private void confirmReception(){
        String message = "V";
        if (tx == null || message == null || message.isEmpty()) {
            // Do nothing if there is no device or message to send.
            return;
        }
        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
        tx.setValue(message.getBytes(Charset.forName("UTF-8")));
        if (gatt.writeCharacteristic(tx)) {
            writeLine("Sent: " + message);
        }
        else {
            writeLine("Couldn't write TX characteristic!");
        }
    }

    /**
     * Scan for all BTLE devices.
     * Handles the Bluetooth activation
     * See the code in initScanCallback
     */
    public void startScan() {
        // Scan for all BTLE devices.
        // The first one with the UART service will be chosen--see the code in the scanCallback.
        writeLine("Scanning for devices...");
        if(scanCallback ==null){
            initScanCallback();
        }
        if(this.adapter.getBluetoothLeScanner() == null){
            checkBluetoothConnection();
            return;
        }
        this.adapter.getBluetoothLeScanner().startScan(scanCallback);
    }

    /**
     * Instantiates the scanCallback object
     * The scan stops when a device with the hardcoded name is  found
     */
    public void initScanCallback() {

        this.scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                // AJO : je permets plusieurs noms
                String devName = result.getDevice().getName();
                writeLine("Found device: "+devName);
                if (devName != null && !devName.isEmpty()) {
                    devName = devName.toUpperCase();
                    if ((Objects.equals(devName, "GO-SAFE")) || (devName.contains("AIDEV"))) {
                        adapter.getBluetoothLeScanner().stopScan(scanCallback);
                        deviceDetected = result.getDevice();
                        isDeviceFound = true;
                        gatt = deviceDetected.connectGatt(activity, false, callback);
                        Log.d("Success:", "Device found !");
                    }
                }
            }

            /**
             * Callback when batch results are delivered.
             *
             * @param results List of scan results that are previously scanned.
             */
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            /**
             * Callback when scan could not be started.
             *
             * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
             */
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }

    /**
     * Main BTLE device callback where much of the logic occurs.
     */
    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        // Called whenever the device connection state changes, i.e. from disconnected to connected.
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                writeLine("Connected!");
                // Discover services.
                if (!gatt.discoverServices()) {
                    writeLine("Failed to start discovering services!");
                }
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                writeLine("Disconnected!");
                createNotification("Connection Status", "Disconnected");
                isDeviceFound = false;
                startScan();
            }
            else {
                Log.d("State changed", " New state: "+newState);
            }
        }

        /**
         * Called when services have been discovered on the remote device.
         * It seems to be necessary to wait for this discovery to occur before
         * manipulating any services or characteristics.
         * @param gatt BluetoothGatt object providing communication smart devices
         * @param status Status of the bluetooth connectivity
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("Discovery completed!","OK");
            }
            else {
                Log.d("Discovery failed", "status: " + status);
            }
            // Save reference to each characteristic.
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(rx, true)) {
                Log.d("RX Notification","Failure");
            }
            // Next update the RX characteristic's client descriptor to enable notifications.
            if (rx.getDescriptor(CLIENT_UUID) != null) {
                BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (!gatt.writeDescriptor(desc)) {
                    Log.d("Error","RX client descriptor!");
                    if (gatt != null) {
                        // For better reliability be careful to disconnect and close the connection.
                        gatt.disconnect();
                        gatt.close();
                        gatt = null;
                        tx = null;
                        rx = null;
                    }
                    isDeviceFound = false;
                    startScan();
                }
                else {
                    writeLine("connection OK");
                    createNotification("Connection Status", "Ready !");
                }
            }
            else {
                Log.d("Missing :","RX client descriptor!");
                if (gatt != null) {
                    // For better reliability be careful to disconnect and close the connection.
                    gatt.disconnect();
                    gatt.close();
                    gatt = null;
                    tx = null;
                    rx = null;
                }
                isDeviceFound = false;
                startScan();
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d("Received: ", "" + characteristic.getStringValue(0));
            String braceletData = characteristic.getStringValue(0);
            sendPosts(braceletData);
            createNotification("Aidevig","Alert received");
            return;
        }
    };

    /**
     * Sets the level of the alert data sent to the Server
     * @param letter : the first letter if the data sent by the Aidevig device
     * @return int corresponding to the level according to the user's bracelet data
     */
    private int getLevel(String letter){
        if(Objects.equals("B", letter)){
            return 1;
        }else if(Objects.equals("C", letter) || Objects.equals("S", letter)){
            return 2;
        }else {
            return 0;
        }
    }

    /**
     * Used whenever the bracelet is triggered
     * @param braceletData sent to the Aidevig WebServer
     */
    public void sendPosts(String braceletData){
        JSONObject toSend = new JSONObject();
        writeLine(braceletData.substring(1, braceletData.length()));
        writeLine(String.valueOf(braceletData.charAt(0)));
        try {
            // AJO : removing date => the date will be automatically provided by the backend
            // toSend.put("creation", DateFormat.getDateTimeInstance().format(new Date()));
            if(mLastLocation !=null){
                toSend.put("latitude", mLastLocation.getLatitude());
                toSend.put("longitude", mLastLocation.getLongitude());
            }
            toSend.put("level", getLevel(braceletData.substring(0,1)));
            toSend.put("pulse", Integer.parseInt(braceletData.substring(1, braceletData.length())));
            toSend.put("trigger", braceletData.substring(0,1));
            // temporary : retrieving hardcoded user_id
            // fix this : should retrieve this id from something that can be changed on the app
            String userIdString = this.activity.getString(R.string.personal_id);
            toSend.put("user_id", userIdString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Sent :",toSend.toString());
        postRequests.addToRequestQueue(new JsonObjectRequest(Request.Method.POST, AidVigeUrls.ALERT, toSend, new Response.Listener<JSONObject>() {
            /**
             * Called when a response is received.
             *
             * @param response
             */
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Success", response.toString());
                //result = response;
                try {
                    if (response.getInt("level") != 0) {
                        confirmReception();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            /**
             * Callback method that an error has been occurred with the
             * provided error code and optional user-readable message.
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Pulse error", error.toString());
            }
        }));
    }


    /**
     *  Called when the connection is stopped
     */

    public void onStop(){
        if (gatt != null) {
            // For better reliability be careful to disconnect and close the connection.
            gatt.disconnect();
            gatt.close();
            gatt = null;
            tx = null;
            rx = null;
        }
        Log.d("Stop", "BluetoothManager process stopped");
    }

    /**
     * BTLE device scanning callback.
     * Filtering by custom UUID is broken in Android 4.3 and 4.4, see:
     *  http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation?noredirect=1#comment27879874_18019161
     * This is a workaround function from the SO thread to manually parse advertisement data.
     * @param advertisedData
     * @return
     */
    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            //Log.e(LOG_TAG, e.toString());
                            continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }

    /**
     * Checking bluetooth connection
     * A dialog will appear requesting user permission to enable Bluetooth, as shown in Figure 1. If the user responds "Yes," the system will begin to enable Bluetooth and focus will return to your application once the process completes (or fails).
     */
    public void checkBluetoothConnection(){
        if(!this.getAdapter().isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}

