package com.darpal.foodlabrinthnew.MapView;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {
    private String googlePlaceData, url;
    private GoogleMap mMap;
    @Override
    public String doInBackground(Object... objects)
    {
        mMap=(GoogleMap)objects[0];
        url= (String) objects[1];

        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googlePlaceData=downloadUrl.RealTheUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    public void onPostExecute(String s) {
        List<HashMap<String,String>> nearByPlaceList = null;
        DataParser dataParser=new DataParser();
        nearByPlaceList=dataParser.parse(s);
        DisplayNearbyPlaces(nearByPlaceList);

    }

    public void DisplayNearbyPlaces(List<HashMap<String,String>> nearByPlaceList)
    {
        for(int i=0; i<nearByPlaceList.size();i++)
        {
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String> googleNearbyPlace = nearByPlaceList.get(i);
            String nameOfPlace = googleNearbyPlace.get("Place Name");
            String vicinity = googleNearbyPlace.get("Vicinity");
            double Latitude=Double.parseDouble(googleNearbyPlace.get("Latitude"));
            double Longitude=Double.parseDouble(googleNearbyPlace.get("Longitude"));

            LatLng latLng=new LatLng(Latitude,Longitude);

            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + ":" + vicinity );
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));



        }
    }
}
