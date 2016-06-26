package com.mybustrip.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;
import com.mybustrip.R;
import com.mybustrip.config.Constants;
import com.mybustrip.config.IntentFilters;
import com.mybustrip.config.PublicStopManager;
import com.mybustrip.model.events.NearPublicStopAvailableEvent;
import com.mybustrip.model.events.NearPublicStopDoneEvent;
import com.mybustrip.model.events.PublicStopRealtimeInfoAvailableEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboFragmentActivity;

public class MainActivity extends RoboFragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private GoogleMap googleMap;

    private Location lastKnowLocation;

    private GoogleApiClient client;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Inject
    private Bus bus;

    @Inject
    private PublicStopManager publicStopManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        client.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        client.disconnect();
    }

    @Subscribe
    public void newPublicStopAvailable(final NearPublicStopAvailableEvent event) {
        publicStopManager.addPublicStop(event.stop);
    }

    @Subscribe
    public void nearPublicStopDone(final NearPublicStopDoneEvent event) {
        Toast.makeText(this, "I was able to find " + event.total + " bus stops nearby", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void realtimeInfoAvailable(final PublicStopRealtimeInfoAvailableEvent event) {
        publicStopManager.addRealtimeInfo(event.stopId, event.realtimeRoutes);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            Log.d(LOG_TAG, "On client connect");
            Log.d(LOG_TAG, "Coordinates Lat x Long: " + lastKnowLocation.getLatitude() + "x" + lastKnowLocation.getLongitude());
            updateMapLocation();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "On client connection suspended");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        publicStopManager.initialize(googleMap, this);

        Log.d(LOG_TAG, "On map ready");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            updateMapLocation();
        } else {
            requestPermissions();
        }

    }

    @TargetApi(value = 23)
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                handleMyLocationPermission();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateMapLocation() {
        if (lastKnowLocation != null && googleMap != null) {
//            lastKnowLocation.setLatitude(53.386394);
//            lastKnowLocation.setLongitude(-6.2470447);
            obtainPublicStopList();
            final LatLng myLocation = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
    }

    private void handleMyLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Permission Denied
            Toast.makeText(MainActivity.this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void obtainPublicStopList() {
        Intent intent = new Intent(IntentFilters.LIST_NEARBY_STOPS);
        intent.setPackage(getPackageName());
        intent.putExtra(Constants.KNOW_POSITION, lastKnowLocation);
        startService(intent);
    }

}
