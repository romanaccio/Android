/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.dty.gosafe.activity;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dty.gosafe.connection.BluetoothManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   HomeFragment.OnFragmentInteractionListener,
                   ProfileFragment.OnFragmentInteractionListener,
                   StatsFragment.OnFragmentInteractionListener,
                   ToolsFragment.OnFragmentInteractionListener,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener,
                   LocationListener {

    /**
     * drawer : top-level container, allows the interactive "drawer" views to be pulled out from the edge of the window
     * navigationView : Standard navigation menu for the application
     *
     * GoogleApiClient to use Google Services such as Location Services, Android Map API
     *
     *
     * mLastLocation : The location to the server at the initialisation of the application
     * mLocationRequest :
     * mRequestingLocationUpdates : true the app is requesting Location Updates
     *
     * BluetoothManager : Manager of the connectivity to the AideVig' bracelet
     * For more details, refer to the BluetoothManager class
     */

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;


    private BluetoothManager BA;


    /**
     * Called when the Activity is first created. The normal static set up is done here.
     * For better understanding, refer to the Activity lifecycle
     * @param savedInstanceState : a a Bundle from the previously saved state, if there was ever one
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Gets an instance of the bluetooth manager
        BA = BluetoothManager.getInstance(this);
        // Starts location requests
        createLocationRequest();

        // FloatinButton, disabled for the map to get focus
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // Gets the main drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Instantiates the main view with the HomeFragement
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_map));
    }

    /**
     * Called when the activity will start interacting with the user
     */
    @Override
    protected void onResume() {
        super.onResume();
        BA.startScan();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Called when the activity is becoming visible to the user
     */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
     * This may happen either because a new activity is being started, an existing one is being brought in front of this one, or this one is being destroyed.
     * Called right before the activity loses foreground focus.  Close the BTLE connection.
     */
    @Override
    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        BA.onStop();
        super.onStop();
    }


    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        if(drawer ==null)
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     * This is only called once, the first time the options menu is displayed.
     * @param menu
     * @return true if the item was added
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item : An item in the menu that is selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // no inspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called whenever a navigation item in your action bar is selected.
     * @param item : an Item listed in the menu
     * @return true if the selected Item is displayed
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = new HomeFragment();
        //String title = getString(R.string.app_name);
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            fragment = new HomeFragment();
            //title = getString(R.string.title_map);
            // Handle the map action
        } else if (id == R.id.nav_stats) {
            fragment = new StatsFragment();
            //title = getString(R.string.title_stats);
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            //title = getString(R.string.title_profile);
        } else if (id == R.id.nav_manage) {
            fragment = new ToolsFragment();
            //title = getString(R.string.title_manage);
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle(item.getTitle());


        if(drawer ==null){
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }

        drawer.closeDrawers();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * Here to request the missing permissions, and then overriding
     * public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
     * to handle the case where the user grants the permission.
     * See the documentation for ActivityCompat#requestPermissions for more details.
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},permissionCheck
                );
                // The callback method gets the result of the request.
            }
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        if (mLastLocation != null) {
            BA.setLastLocation(mLastLocation);
        }
        return;
    }


    /**
     * Gets the update using the LocationListener callback approach.
     * Call requestLocationUpdates(), passing it your instance of the GoogleApiClient, the LocationRequest object, and a LocationListener
     * startLocationUpdates is called from the onConnected() callback
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mRequestingLocationUpdates = true;
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    /**
     * GoogleAPI connection callback
     * Called when the client is temporarily in a disconnected state.
     * Disable any UI components that depend on Google APIs until onConnected() is called.
     * @param i : the reason of disconnection
     */
    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
        Log.d("Network Disconnected", String.valueOf(i));
    }

    /**
     * Called when there was an error connecting the client to the service.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
        Log.d("Connection failure", connectionResult.getErrorMessage());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }


    // This code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            //
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * Creates a LocationRequest.
     * The parameters determine the levels of accuracy requested.
     * Sets the rate in milliseconds at which your app prefers to receive location updates
     * Sets the fastest rate in milliseconds at which your app can handle location updates. The Google Play services location APIs send out updates at the fastest rate that any app has requested with setInterval(). If this rate is faster than your app can handle, you may encounter problems with UI flicker or data overflow. To prevent this, call setFastestInterval() to set an upper limit to the update rate.
     * Sets the priority of the request, which gives the Google Play services location services a strong hint about which location sources to use
     * For details, please refer to the LocationRequest class reference.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Called whenever the location is updated
     * @param location : the Location object containing the location's latitude and longitude
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        BA.setLastLocation(location);
    }
}
