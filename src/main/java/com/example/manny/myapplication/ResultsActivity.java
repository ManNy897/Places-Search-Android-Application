package com.example.manny.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private List<Results> resultsList = new ArrayList<>();
    private Results result;
    private List<Results.Result> currResultsList;
    private String nextPage;
    private int pageNum;
    private ImageButton heart;
    private Button previousButton, nextButton;
    private RequestObj request;
    private TextView message;
    private ProgressDialog dialog;
    private SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle("Search Results");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        shared = this.getSharedPreferences("results", Context.MODE_PRIVATE);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Fetching results");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        //String json = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String json = shared.getString("json", "");
        request = new RequestObj(this);
        pageNum = 0;
        prepareResults(json, pageNum);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ResultsAdapter(this.currResultsList, request, this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        heart = (ImageButton) findViewById(R.id.heart);
        previousButton = (Button) findViewById(R.id.button_prev);
        previousButton.setEnabled(false);
        nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setEnabled(nextPage != null);
        message = (TextView) findViewById(R.id.no_results);

        if(currResultsList.isEmpty()){
            message.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            message.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }


    }

    private void prepareResults(String json_string, int pageNum){
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(json_string);
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }

        Gson gson = new Gson();
        this.result = gson.fromJson(json_string, Results.class);
        this.currResultsList = this.result.getResults(pageNum);
        this.nextPage = result.getNextPage();
//        Log.d("next page", this.nextPage);
    }

    public void previousOnClick(View view){
        pageNum--;
        if(pageNum == 0){
            previousButton.setEnabled(false);
        }
        nextButton.setEnabled(true);
        this.currResultsList.clear();
        this.currResultsList.addAll(this.result.getResults(pageNum));
        this.mAdapter.notifyDataSetChanged();
    }

    public void nextOnClick(View view){
        if(result.getSize() > pageNum*20+20){
            currResultsList.clear();
            currResultsList.addAll(result.getResults(++pageNum));
            if(pageNum == 2){
                nextButton.setEnabled(false);
            }
            previousButton.setEnabled(true);
            mAdapter.notifyDataSetChanged();
        }else {
            nextPageRequest(this.nextPage);
        }

    }

    private void nextPageRequest(String pagetoken){
        // Instantiate the RequestQueue.
        Log.d("send", "sending request");
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8081/api";
        try {
            String query = "?pagetoken=" + URLEncoder.encode(pagetoken, "UTF-8");
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
                        Gson gson = new Gson();
                        Results newResult = gson.fromJson(response, Results.class);
                        dialog.hide();
                        if(newResult.getSize() > 0) {
                            currResultsList.clear();
                            result.addToList(newResult.getResults());
                            currResultsList.addAll(result.getResults(++pageNum));
                            Log.d("next page bef", nextPage);
                            nextPage = newResult.getNextPage();
//                        Log.d("next page after", nextPage);
                            if (nextPage == null) {
                                nextButton.setEnabled(false);
                            }
                            mAdapter.notifyDataSetChanged();
                            previousButton.setEnabled(true);
                        }
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

}
