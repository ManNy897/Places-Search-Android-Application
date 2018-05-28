package com.example.manny.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;
    private Details details;
    private LatLngBounds lat_lng_bounds;
    private AutoCompleteTextView fromSearch;
    private FragmentActivity myContext;
    private RequestObj request;
    private GoogleMap map;
    private Spinner mode;
    private List<Polyline> polylineFinal = new ArrayList<Polyline>();
    private String selectedPlaceId;
    private Marker marker;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        request = new RequestObj(view.getContext());
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        String json_string = getArguments().getString("json_string");
        Gson gson = new Gson();
        details = gson.fromJson(json_string, Details.class);

        double lat = details.result.geometry.location.lat;
        double lng = details.result.geometry.location.lng;
        lat_lng_bounds = new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng));
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, lat_lng_bounds, new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build());
        fromSearch = (AutoCompleteTextView) view.findViewById(R.id.map_from_text);
        fromSearch.setAdapter(mPlaceAutocompleteAdapter);

        //SupportMapFragment mapFragment = (SupportMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Log.d("map fragment", mapFragment.toString());
        mapFragment.getMapAsync(this);
        mode = (Spinner) view.findViewById(R.id.mode);
        fromSearch.setOnItemClickListener(mAutocompleteClickListener);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        googleMap.addMarker(new MarkerOptions().position(lat_lng_bounds.getCenter()).title(details.result.name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng_bounds.getCenter(), 12.0f));
        map  = googleMap;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
         // hideSoftKeyboard();
          final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
          selectedPlaceId = item.getPlaceId();
          directionsRequest(selectedPlaceId, details.result.place_id, mode.getSelectedItem().toString().toLowerCase());
          mode.setOnItemSelectedListener(mSpinnerSelectListener);

          mGeoDataClient.getPlaceById(selectedPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
              @Override
              public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                  if (task.isSuccessful()) {
                      PlaceBufferResponse places = task.getResult();
                      Place myPlace = places.get(0);
                      Log.i("map fragment", "Place found: " + myPlace.getName());
                      if(marker != null){
                          marker.remove();
                      }
                      marker = map.addMarker(new MarkerOptions().position(myPlace.getLatLng()));
                      places.release();
                  } else {
                      Log.e("map fragment", "Place not found.");
                  }
              }
          });
      }
    };

    private AdapterView.OnItemSelectedListener mSpinnerSelectListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
            // hideSoftKeyboard();
//            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
//            final String placeId = item.getPlaceId();
            if(!fromSearch.getText().toString().isEmpty()) {
                directionsRequest(selectedPlaceId, details.result.place_id, mode.getSelectedItem().toString().toLowerCase());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };


    private void directionsRequest(String fromPlaceId, String toPlaceId, String mode){
        // Instantiate the RequestQueue.
        Log.d("send", "sending request");
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8081/api";
        try {
            String query = "?origin=" + URLEncoder.encode(fromPlaceId, "UTF-8");
            query += "&destination=" + URLEncoder.encode(toPlaceId, "UTF-8");
            query += "&mode=" + URLEncoder.encode(mode, "UTF-8");
            url += query;
        }catch(UnsupportedEncodingException e){
            System.err.println(e);
        }
        Log.d("url", url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        String[] directionsList = parseDirections(response);
                        displayDirections(directionsList);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
        request.addToRequestQueue(stringRequest);
        // queue.add(stringRequest);
    }

    private void displayDirections(String[] directionsList){
        int cnt = directionsList.length;
        clearPolylines();
        for(int i = 0; i < cnt; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));
            polylineFinal.add(map.addPolyline(options));
        }
    }

    private void clearPolylines(){
        for(Polyline line: polylineFinal){
            line.remove();
        }
        polylineFinal.clear();
    }

    private String[] parseDirections(String json_string){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    private String[] getPaths(JSONArray googleStepsJson){
        int cnt = googleStepsJson.length();
        String[] polyLines = new String[cnt];
        for(int i = 0; i < cnt; i++){
            try {
                polyLines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polyLines;
    }

    private String getPath(JSONObject googlePathJson){
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
