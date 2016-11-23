package com.amoxpoua.routetrackdemo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

public class RouteTrackActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "RouteTrackActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate event was called");
        super.onCreate(savedInstanceState);

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_route_track);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart event was called");
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop event was called");
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected event was called");
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.i(TAG, "Starting location updates");
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended event was called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed event was called");
        Log.d(TAG, "connectionResult error message: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged event was called");
        mCurrentLocation = location;
        Log.d(TAG, "mCurrentLocation was updated with Latitude: " + mCurrentLocation.getLatitude() + ", Longitude: " + mCurrentLocation.getLongitude());
        addMarker();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause event was called");
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        Log.i(TAG, "Stopping location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume event was called");
        super.onResume();
        Log.d(TAG, "mGoogleApliclient.isConnected(): " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady event was called");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void addMarker() {
        Log.i(TAG, "Adding marker");
        MarkerOptions options = new MarkerOptions();

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);

        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
    }
}
