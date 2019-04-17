package com.darpal.foodlabrinthnew.MapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.darpal.foodlabrinthnew.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude,longitude;
    private int ProximityRadius=10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClick(View v){
        String restaurant="restaurant";
        Object transferData[]=new Object[2];
        GetNearbyPlaces getNearbyPlaces= new GetNearbyPlaces();
        switch (v.getId())
        {
            case R.id.restuarant_nearby:
                mMap.clear();
                String url = getUrl(latitude,longitude,restaurant);
                transferData[0]=mMap;
                transferData[1]=url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"Searching for Nearby Restaurants",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing for Nearby Restaurants",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("Location=" + latitude + "," + longitude);
        googleUrl.append("&radius = " + ProximityRadius);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key=" + "AIzaSyDLABrbJwgQ7AtiJ5rXYqGjp4Tfn6c2g1c");

        Log.d("MapsActivity","url=" + googleUrl.toString());
        return googleUrl.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        lastLocation=location;
        if(currentUserLocationMarker!=null)
        {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient!=null)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
    }


    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, (LocationListener) this);

    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        }
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient==null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    public void onConnectionSuspended(int i) {

    }
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
