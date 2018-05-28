package com.example.manny.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FavoritesResultsAdapter extends RecyclerView.Adapter<FavoritesResultsAdapter.MyViewHolder> {
    public List<Results.Result> resultsList;
    private RequestObj request;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.results.MESSAGE";
    private SharedPreferences sharedPreferences;
    private TextView message;
    private RecyclerView recyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, address;
        public ImageView img;
        public ImageButton heart;
        public String id;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            address = (TextView) view.findViewById(R.id.address);
            img = (ImageView) view.findViewById(R.id.img);
            heart = (ImageButton) view.findViewById(R.id.heart);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDetails(id, v);
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, DetailsActivity.class);
//                    context.startActivity(intent);
                }
            });
        }
    }

    public FavoritesResultsAdapter(List<Results.Result> resultsList, RequestObj request, Context context, View view) {
        this.resultsList = resultsList;
        this.request = request;
        sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        message  = (TextView) view.findViewById(R.id.no_favorites);
        recyclerView = (RecyclerView) view.findViewById(R.id.favs_recycler_view);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorites_result_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public void onNewData(ArrayList<Results.Result> newData){

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Results.Result result = resultsList.get(position);
        holder.id = result.getId();
        holder.title.setText(result.getTitle());
        holder.address.setText(result.getAddress());
        Picasso.get().load(result.getImg()).resize(150, 150).into(holder.img);
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("image value", holder.heart.getTag().toString());
//                Log.d("heart value", R.drawable.heart_empty+"");
            resultsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,resultsList.size());
            sharedPreferences.edit().remove(result.getId()).apply();
            if(resultsList.isEmpty()){
                message.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else{
                message.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            }
        });
        //holder.img.setImage(result.getImg());
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public void updateResultsListItems(List<Results.Result> newResults) {
        final PageDiffCallback diffCallback = new PageDiffCallback(this.resultsList, newResults);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.resultsList.clear();
        this.resultsList.addAll(newResults);
        diffResult.dispatchUpdatesTo(this);
    }

    private void getDetails(String id, final View v){
        // Instantiate the RequestQueue.
        Log.d("send", "sending request");
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8081/api";
        try {
            String query = "?placeid=" + URLEncoder.encode(id, "UTF-8");
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
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, response);
                        context.startActivity(intent);

//                        Intent intent = new Intent(this, ResultsActivity.class);
//                        intent.putExtra(EXTRA_MESSAGE, json);
//                        startActivity(intent);
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
}