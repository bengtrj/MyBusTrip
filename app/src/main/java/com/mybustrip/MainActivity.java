package com.mybustrip;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;
import com.mybustrip.config.Constants;
import com.mybustrip.config.IntentFilters;
import com.mybustrip.model.PublicStop;
import com.mybustrip.model.RealtimeRoute;
import com.mybustrip.model.events.NearByStopListAvailableEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboFragmentActivity;

public class MainActivity extends RoboFragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private Location lastKnowLocation;

    private GoogleApiClient client;

    @Inject
    private Bus bus;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
//        mMap.setOnMarkerClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void newListAvailable(final NearByStopListAvailableEvent event) {
        Log.d(LOG_TAG, "Stops: " + event.stops.size());
        for (PublicStop publicStop : event.stops) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(publicStop.getLatitude(), publicStop.getLongitude()))
                            .title(publicStop.getStopId())
                            .snippet(generateSnippet(publicStop))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_71))
                            );
        }

    }

    private String generateSnippet(PublicStop publicStop) {

        StringBuilder builder = new StringBuilder();
        if (publicStop.getRealtimeRoutes() != null && !publicStop.getRealtimeRoutes().isEmpty()) {
            for (RealtimeRoute route : publicStop.getRealtimeRoutes()) {
                builder.append("[").append(route.getRoute()).append("]").append(": ");
                builder.append(route.getDuetime());
                if (!route.getDuetime().equalsIgnoreCase("due")) {
                    builder.append("min");
                }
                builder.append("; ");
            }
            builder.deleteCharAt(builder.length() - 2);
        } else {
            for (String route : publicStop.getRoutes()) {
                builder.append(route).append(", ");
            }
            builder.deleteCharAt(builder.length() - 2);
        }

        return builder.toString();
//        final SpannableString snippetText = new SpannableString(builder);
//
//        if (builder.length() > 2) {
//            int indexOf = builder.indexOf("[");
//            while (indexOf > 0) {
//                snippetText.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOf, builder.indexOf("]", indexOf), 0);
//                indexOf = builder.indexOf("[", indexOf);
//            }
//        }
//
//        return snippetText;
    }

    private void obtainPropertyList() {
        Intent intent = new Intent(IntentFilters.LIST_NEARBY_STOPS);
        intent.setPackage(getPackageName());
        intent.putExtra(Constants.KNOW_POSITION, lastKnowLocation);
        startService(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            Log.d(LOG_TAG, "Coordinates Lat x Long: " + lastKnowLocation.getLatitude() + "x" + lastKnowLocation.getLongitude());
            updateMapLocation();
            obtainPropertyList();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            updateMapLocation();
        } else {
            requestPermissions();
        }

    }

    private void updateMapLocation() {
        if (lastKnowLocation != null && mMap != null) {
            final LatLng myLocation = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
    }

    @TargetApi(value = 23)
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_PERMISSIONS);
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

    private void handleMyLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Permission Denied
            Toast.makeText(MainActivity.this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        client.connect();

//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.mybustrip/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.mybustrip/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);

        client.disconnect();
    }
}
