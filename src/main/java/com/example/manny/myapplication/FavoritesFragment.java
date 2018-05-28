package com.example.manny.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Results result;
    private List<Results.Result> currFavoritesList;
    private RequestObj request;
    private SharedPreferences sharedPreferences;
    private TextView message;



    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        request = new RequestObj(getActivity());
        sharedPreferences = getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        currFavoritesList = new ArrayList<Results.Result>();
        prepareResults(sharedPreferences, currFavoritesList);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.favs_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FavoritesResultsAdapter(this.currFavoritesList, request, getContext(), view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        message = (TextView) view.findViewById(R.id.no_favorites);

        if(currFavoritesList.isEmpty()){
            message.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            message.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void prepareResults(SharedPreferences sharedPreferences, List<Results.Result> currList){
        Gson gson = new Gson();
        Map<String,?> keys = sharedPreferences.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            currList.add(gson.fromJson(entry.getValue().toString(), Results.Result.class));
        }
    }




}
