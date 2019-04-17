package com.darpal.foodlabrinthnew.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    public HashMap<String, String > getSingleNearbyPlace(JSONObject googlePlaceJson)
    {

        HashMap<String,String> googlePlaceMap=new HashMap<>();
        String NameOfPlace="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String referece="";

        try {
            if(!googlePlaceJson.isNull("name"))
            {
                NameOfPlace = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");

            longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            referece = googlePlaceJson.getString("reference");

            googlePlaceMap.put("Place Name", NameOfPlace);
            googlePlaceMap.put("Vicinity", vicinity);
            googlePlaceMap.put("Latitude", latitude);
            googlePlaceMap.put("Longitude", longitude);
            googlePlaceMap.put("reference", referece);


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return googlePlaceMap;

    }

    public List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        List<HashMap<String,String>> NearbyPlacesList = new ArrayList<>();
        HashMap<String,String> NearbyPlaceMap=null;

        for(int i=0; i<counter;i++)
        {
            try {
                NearbyPlaceMap=getSingleNearbyPlace((JSONObject)jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlaceMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return NearbyPlacesList;
    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
