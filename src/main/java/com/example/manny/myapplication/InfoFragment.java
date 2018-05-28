package com.example.manny.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;




public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Details details;
    private SectionsPageAdapter mSectionsPageAdapter;
    private TextView address, phone_number, price_level, google_page, website;
    private RatingBar rating;

    public InfoFragment() {
    }

    // TODO: Rename and change types and number of parameters
//    public static InfoFragment newInstance(String param1, String param2) {
//        InfoFragment fragment = new InfoFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        address = (TextView) view.findViewById(R.id.address_value);
        phone_number = (TextView) view.findViewById(R.id.phone_number_value);
        price_level = (TextView) view.findViewById(R.id.price_level_value);
        rating = (RatingBar) view.findViewById(R.id.ratingBar);
        google_page = (TextView) view.findViewById(R.id.google_page_value);
        website = (TextView) view.findViewById(R.id.website_value);

        String json_string = getArguments().getString("json_string");
        Gson gson = new Gson();
        details = gson.fromJson(json_string, Details.class);

        address.setText(details.result.vicinity);
        phone_number.setText(details.result.formatted_phone_number);
        phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=details.result.formatted_phone_number;

                Intent i=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phone));
                startActivity(i);
            }
        });

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < details.result.price_level; i++) {
            builder.append("$");
        }

        price_level.setText(builder.toString());
        rating.setRating(details.result.rating);
        google_page.setText(details.result.url);
        google_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=details.result.url;

                Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                startActivity(i);
            }
        });
        website.setText(details.result.website);
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String website=details.result.website;

                Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse(website));
                startActivity(i);
            }
        });

        return view;
    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
