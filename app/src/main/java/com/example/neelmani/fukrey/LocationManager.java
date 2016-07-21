package com.example.neelmani.fukrey;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.neelmani.fukrey.Fragments.AreaUpdateFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationManager  implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final long LOCATION_REQUEST_INTERVAL = 5*1000;
    private static final long FASTEST_LOCATION_REQUEST_INTERVAL = 5*1000;
    public static LocationRequest locationRequest;
    public static GoogleApiClient googleApiClient;
    public static Location currentLocation,lastLocation;
    public static String lastUpdateTime;
    public static Boolean isFristTimeLocationChanged;
    private static final String TAG = "LocationManager";

    public LocationManager(Context contextReceived) {

        createLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(contextReceived)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        isFristTimeLocationChanged=true;
    }


    protected  void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_LOCATION_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected..................: " + googleApiClient.isConnected());
        startLocationUpdates();

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
  }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingRslt = LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        Log.d(TAG, "Location update started .................: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed......................: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged......................................");
        currentLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if(isFristTimeLocationChanged)
        {
            AreaUpdateFragment.RefreshAUF(-1);
            isFristTimeLocationChanged=false;
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        Log.d(TAG, "Location update stopped ...........................");
    }
}
