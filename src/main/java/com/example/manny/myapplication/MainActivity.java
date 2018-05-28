package com.example.manny.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Places Search");
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        //SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        FormFragment form = new FormFragment();
        mSectionsPageAdapter.addFragment(form, "Search");
        FavoritesFragment favorites = new FavoritesFragment();
        mSectionsPageAdapter.addFragment(favorites, "Favorites");

        viewPager.setAdapter(mSectionsPageAdapter);
    }

//    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION_FINE = 1;
//    private TextInputEditText inputKey, inputDistance, inputLocation;
//    private TextInputLayout inputLayoutKey, inputLayoutDistance, inputLayoutLocation;
//    private Spinner category;
//    private RadioGroup radioGroup;
//    private RequestObj request;
//    private LocationManager locationManager;
//    public static Location locationValue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        System.out.println("created");
//        Log.d("Created", "Created");
//        request = new RequestObj(this);
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        checkPermissions();
//        inputKey = (TextInputEditText) findViewById(R.id.editKey);
//        category = (Spinner) findViewById(R.id.category);
//        inputDistance = (TextInputEditText) findViewById(R.id.distance);
//        inputLocation = (TextInputEditText) findViewById(R.id.otherLocation);
//        inputLocation.setFocusable(false);
//        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        inputLayoutKey = (TextInputLayout) findViewById(R.id.input_layout_editKey);
//        inputLayoutLocation = (TextInputLayout) findViewById(R.id.input_layout_otherLocation);
//        inputLayoutDistance = (TextInputLayout) findViewById(R.id.input_layout_distance);
//        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId)
//            {
//                if(checkedId == R.id.radioCurrent){
//                    //disable text box
//                    inputLayoutLocation.setErrorEnabled(false);
//                    inputLocation.setFocusable(false);
//                }
//                else{
//                    //
//                    inputLocation.setFocusableInTouchMode(true);
//                }
//            }
//        });
//    }
//
//    private void getLocation(){
//        Log.d("getting loc", "getting location");
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                //makeUseOfNewLocation(location);
//                Log.d("location", "new location retrieved");
//                Log.d("location is", location.toString());
//                locationValue = location;
//            }
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//            public void onProviderEnabled(String provider) {}
//
//            public void onProviderDisabled(String provider) {}
//        };
//        try {
//            Log.d("in try", "entered try");
//            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,null);
//        } catch (SecurityException e) {
//            Log.d("permission", "permission not granted");
//            // dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
//        }
//    }
//
//    private void checkPermissions(){
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_LOCATION_FINE);
//
//        }else{
//            getLocation();
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION_FINE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    getLocation();
//
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }
//
//
//    public void onSubmit(View view){
//        System.out.println("entered submit");
//        Log.d("Submitted", "In submit function");
//        boolean err = false;
//        if(!validateKey()){
//            err = true;
//        }
//        if(inputLocation.isFocusable() && !validateLocation()){
//            err = true;
//        }
//        if(!err) {
////            String query = "?key=" + inputKey.getText().toString();
////            query += (inputDistance.getText().toString().trim().isEmpty()) ? ""
//            sendRequest();
//        }
//    }
//
//    public void onClear(View view){
//        inputKey.setText("");
//        inputDistance.setText("");
//        inputLocation.setText("");
//        radioGroup.check(R.id.radioCurrent);
//    }
//
//    private boolean validateKey(){
//        if (inputKey.getText().toString().trim().isEmpty()) {
//            Log.d("inpult", inputLayoutKey.toString());
//            inputLayoutKey.setErrorEnabled(true);
//            inputLayoutKey.setError(getString(R.string.err_msg));
//            requestFocus(inputKey);
//            return false;
//        } else {
//            inputLayoutKey.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validateLocation(){
//        if (inputLocation.getText().toString().trim().isEmpty()) {
//            inputLayoutLocation.setError(getString(R.string.err_msg1));
//            requestFocus(inputLocation);
//            return false;
//        } else {
//            inputLayoutLocation.setErrorEnabled(false);
//        }
//        return true;
//    }
//
//    private void requestFocus(View view) {
//        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }
//
//
//    private void sendRequest(){
//        // Instantiate the RequestQueue.
//        Log.d("send", "sending request");
//        //RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://10.0.2.2:8081/api";
//        try {
//        String query = "?keyword=" + URLEncoder.encode(inputKey.getText().toString().trim(), "UTF-8");
//        query += "&category=" + URLEncoder.encode(category.getSelectedItem().toString(), "UTF-8");
//        query += "&distance=" + URLEncoder.encode(((inputDistance.getText().toString().isEmpty()) ? "10" :inputDistance.getText().toString()), "UTF-8");
//        if(radioGroup.getCheckedRadioButtonId() == R.id.radioCurrent){
//            //disable text box
//            query += "&location=" + URLEncoder.encode(locationValue.getLatitude() +","+ locationValue.getLongitude(), "UTF-8");
//        }else{
//            query += URLEncoder.encode("&location_text=" + inputLocation.getText().toString().trim(), "UTF-8");
//        }
//            url += query;
//        }catch(UnsupportedEncodingException e){
//            System.err.println(e);
//        }
//        Log.d("url", url);
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("response", response);
//                        displayResults(response);
//                        // Display the first 500 characters of the response string.
//                        //mTextView.setText("Response is: "+ response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //mTextView.setText("That didn't work!");
//            }
//        });
//        request.addToRequestQueue(stringRequest);
//       // queue.add(stringRequest);
//    }
//
//    private void displayResults(String json){
//        Intent intent = new Intent(this, ResultsActivity.class);
//        intent.putExtra(EXTRA_MESSAGE, json);
//        startActivity(intent);
//    }


}
