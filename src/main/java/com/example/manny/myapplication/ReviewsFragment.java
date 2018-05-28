package com.example.manny.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;


public class ReviewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private SectionsPageAdapter mSectionsPageAdapter;
    private TextView message;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Details.Result.Reviews> reviews;
    private Details details;
    private List<Details.Result.Reviews> googleReviews;
    private List<Details.Result.Reviews> yelpReviews;

    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        String json_string = getArguments().getString("json_string");
        Gson gson = new Gson();
        details = gson.fromJson(json_string, Details.class);
        reviews = details.result.reviews;
        message = (TextView) view.findViewById(R.id.no_reviews);
        googleReviews = details.result.reviews;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter(this.reviews);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        yelpReviews = getYelpReviews();

        if(reviews == null || reviews.isEmpty()){
            message.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            message.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private List<Details.Result.Reviews> getYelpReviews(){
        return null;
    }
}

