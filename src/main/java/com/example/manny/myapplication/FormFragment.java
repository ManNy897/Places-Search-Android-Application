package com.example.manny.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class FormFragment extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION_FINE = 1;
    private TextInputEditText inputKey, inputDistance;
    private TextInputLayout inputLayoutKey, inputLayoutDistance, inputLayoutLocation;
    private Spinner category;
    private RadioGroup radioGroup;
    private RequestObj request;
    private AutoCompleteTextView inputLocation;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;
    private LatLngBounds lat_lng_bounds;
    private LocationManager locationManager;
    public static Location locationValue;
    private Button submit, clear;
    private ProgressDialog dialog;
    private SharedPreferences shared;

    public FormFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        System.out.println("created");
        Log.d("Created", "Created");
        dialog=new ProgressDialog(view.getContext());
        dialog.setMessage("Fetching results");
        dialog.setCancelable(false);
        shared = getContext().getSharedPreferences("results", Context.MODE_PRIVATE);
        dialog.setInverseBackgroundForced(false);
        request = new RequestObj(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        inputKey = (TextInputEditText) view.findViewById(R.id.editKey);
        category = (Spinner) view.findViewById(R.id.category);
        inputDistance = (TextInputEditText) view.findViewById(R.id.distance);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        //encompass whole world
        lat_lng_bounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, lat_lng_bounds, new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build());
        inputLocation = (AutoCompleteTextView) view.findViewById(R.id.otherLocation);
        inputLocation.setAdapter(mPlaceAutocompleteAdapter);
        //inputLocation = (TextInputEditText) view.findViewById(R.id.otherLocation);
        inputLocation.setFocusable(false);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        inputLayoutKey = (TextInputLayout) view.findViewById(R.id.input_layout_editKey);
        inputLayoutLocation = (TextInputLayout) view.findViewById(R.id.input_layout_otherLocation);
        inputLayoutDistance = (TextInputLayout) view.findViewById(R.id.input_layout_distance);
        submit = (Button) view.findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit(v);
            }
        });
        clear = (Button) view.findViewById(R.id.button2);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear(v);
            }
        });
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId == R.id.radioCurrent){
                    //disable text box
                    inputLayoutLocation.setErrorEnabled(false);
                    inputLocation.setFocusable(false);
                }
                else{
                    //
                    inputLocation.setFocusableInTouchMode(true);
                }
            }
        });
        return view;
    }


    private void getLocation(){
        Log.d("getting loc", "getting location");
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                Log.d("location", "new location retrieved");
                Log.d("location is", location.toString());
                locationValue = location;

            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        try {
            Log.d("in try", "entered try");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,null);
        } catch (SecurityException e) {
            Log.d("permission", "permission not granted");
            // dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
        }
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION_FINE);

        }else{
            getLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void onSubmit(View view){
        System.out.println("entered submit");
        Log.d("Submitted", "In submit function");
        boolean err = false;
        if(!validateKey()){
            err = true;
            Toast.makeText(view.getContext(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
        }
        if(inputLocation.isFocusable() && !validateLocation()){
            err = true;
        }
        if(!err) {
//            String query = "?key=" + inputKey.getText().toString();
//            query += (inputDistance.getText().toString().trim().isEmpty()) ? ""
            sendRequest();
        }
    }

    public void onClear(View view){
        inputKey.setText("");
        inputDistance.setText("");
        inputLocation.setText("");
        radioGroup.check(R.id.radioCurrent);
    }

    private boolean validateKey(){
        if (inputKey.getText().toString().trim().isEmpty()) {
            Log.d("inpult", inputLayoutKey.toString());
            inputLayoutKey.setErrorEnabled(true);
            inputLayoutKey.setError(getString(R.string.err_msg));
            requestFocus(inputKey);
            return false;
        } else {
            inputLayoutKey.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLocation(){
        if (inputLocation.getText().toString().trim().isEmpty()) {
            inputLayoutLocation.setError(getString(R.string.err_msg1));
            requestFocus(inputLocation);
            return false;
        } else {
            inputLayoutLocation.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void sendRequest(){
        // Instantiate the RequestQueue.
        Log.d("send", "sending request");
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8081/api";
        try {
            String query = "?keyword=" + URLEncoder.encode(inputKey.getText().toString().trim(), "UTF-8");
            query += "&category=" + URLEncoder.encode(category.getSelectedItem().toString(), "UTF-8");
            query += "&distance=" + URLEncoder.encode(((inputDistance.getText().toString().isEmpty()) ? "10" :inputDistance.getText().toString()), "UTF-8");
            if(radioGroup.getCheckedRadioButtonId() == R.id.radioCurrent){
                //disable text box
                query += "&location=" + URLEncoder.encode(locationValue.getLatitude() +","+ locationValue.getLongitude(), "UTF-8");
            }else{
                query += "&location_text=" + URLEncoder.encode(inputLocation.getText().toString().trim(), "UTF-8");
            }
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
                        dialog.hide();
                        displayResults(response);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
        dialog.show();
        request.addToRequestQueue(stringRequest);
        // queue.add(stringRequest);
    }

    private void displayResults(String json){
        Intent intent = new Intent(getActivity(), ResultsActivity.class);
        //intent.putExtra(MainActivity.EXTRA_MESSAGE, json);
        shared.edit().putString("json", json).apply();
        startActivity(intent);
    }
}
