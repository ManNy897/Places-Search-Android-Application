package com.example.manny.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;

import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


public class PicturesFragment extends Fragment {
    private Details details;
    protected GeoDataClient mGeoDataClient;
    private LinearLayout linearLayout;
    private TextView message;


    public PicturesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.pictures_fragment);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        message = (TextView) view.findViewById(R.id.no_pictures);

        String json_string = getArguments().getString("json_string");
        Gson gson = new Gson();
        details = gson.fromJson(json_string, Details.class);
        getPhotos(details.result.place_id);


        return view;

    }

    private void getPhotos(final String placeId){
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount() == 0){
                    message.setVisibility(View.VISIBLE);
                }
                for(int i = 0; i < photoMetadataBuffer.getCount(); i++){
                    insertImage(photoMetadataBuffer.get(i));
                }
            }

        });
    }

    private void insertImage(PlacePhotoMetadata photoMetadata){
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                ImageView image = new ImageView(linearLayout.getContext());
                image.setImageBitmap(bitmap);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(50,50,50,0);
                image.setLayoutParams(params);
                image.setAdjustViewBounds(true);
                linearLayout.addView(image);
            }
        });
    }




}
