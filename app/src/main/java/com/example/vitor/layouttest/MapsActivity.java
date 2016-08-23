package com.example.vitor.layouttest;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location location;
    double lastLat = 0;
    double lastLong = 0;

    public MapsActivity(){
        Log.i("info", "Constructor passed on maps");
    }

    public void connectGoogleAPI(){
        mGoogleApiClient.connect();
    }

    public void disconnectGoogleAPI(){
        mGoogleApiClient.disconnect();
    }

    public void setLocationRequest(int timer, int fastTimer){
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(timer * 1000)        //  seconds
                .setFastestInterval(fastTimer * 1000); //  seconds

        Log.i("info", "Timer changed to "+ timer + " seconds.");
    }

    public void activeLocationRequest(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("info", "Didnt have permissions to acquired location.");
        }else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void deactiveLocationRequest(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); // Removing Updates When Finished
    }

    public double getLat(){
        return this.lastLat;
    }

    public double getLong(){
        return this.lastLong;
    }

    public LatLng getLatLng(){
        return (location != null) ? new LatLng(location.getLatitude(),location.getLongitude()) : new LatLng(10,20);
    }

    public Location getLocation(){
        return location;
    }

    private void handleNewLocation(Location location) {
        this.location = location;
        Log.i("info", location.toString());
        lastLat = location.getLatitude();
        lastLong = location.getLongitude();
        LatLng latLng = new LatLng(lastLat, lastLong); //Transform to latlng if needed

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectGoogleAPI();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (mGoogleApiClient.isConnected()) {
            deactiveLocationRequest();
            Log.i("info", "Pausing location updates.");
            disconnectGoogleAPI();
        }*/


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) { //Chamado toda vez que é conectado na API do google.
        Log.i("info", "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("info", "Didnt have permissions to acquired location.");
        }else {
            setLocationRequest(4,1); // set timer to 4 seconds
            activeLocationRequest(); // start timer
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); // get the first measure
            if (location == null) {
                Log.i("info", "Location is null...");
                 // For this example we are only going to request location updates when the last location is not known.
            }
            else {
                handleNewLocation(location);
            };
            return;

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("info", "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


}
