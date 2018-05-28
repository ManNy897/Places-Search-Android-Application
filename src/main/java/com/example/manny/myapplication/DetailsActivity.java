package com.example.manny.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private Details details;
    private SharedPreferences sharedPreferences;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        String json = intent.getStringExtra(ResultsAdapter.EXTRA_MESSAGE);
        details = buildDetailsArray(json);
        setTitle(details.result.name);
        sharedPreferences = this.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        gson = new Gson();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager, json);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.info);
        tabLayout.getTabAt(1).setIcon(R.drawable.photos);
        tabLayout.getTabAt(2).setIcon(R.drawable.maps);
        tabLayout.getTabAt(3).setIcon(R.drawable.review);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
       // super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details_activity2, menu);
        menu.getItem(1).setIcon((sharedPreferences.contains(details.result.place_id) ? R.drawable.full_heart : R.drawable.blank_heart ));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_like:
                performLike(item);
                return true;
            case R.id.actionshare:
                performShare(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void performLike(MenuItem item){
        if(sharedPreferences.contains(details.result.place_id)){
            item.setIcon(R.drawable.blank_heart);
            sharedPreferences.edit().remove(details.result.place_id).apply();
            Toast.makeText(this,details.result.name + " was removed from favorites",Toast.LENGTH_SHORT).show();

        }else {
            item.setIcon(R.drawable.full_heart);
            Results.Result result = new Results.Result(details.result.name, details.result.vicinity, details.result.icon, details.result.place_id);
            sharedPreferences.edit().putString(details.result.place_id,  gson.toJson(result)).apply();
            Toast.makeText(this,details.result.name + " was added to favorites",Toast.LENGTH_SHORT).show();

        }
    }

    private void performShare(MenuItem item){
        try {
            String query = "text=" + URLEncoder.encode("Check out " + details.result.name + " located at " + details.result.vicinity, "UTF-8") + " Website: " + "&url=" + URLEncoder.encode(details.result.website, "UTF-8");
            String url = "https://twitter.com/intent/tweet?" + query;
            Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager, String json_string){
        Bundle bundle = new Bundle();
        bundle.putString("json_string", json_string);
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        InfoFragment info = new InfoFragment();
        info.setArguments(bundle);
        adapter.addFragment(info, "Info");
        PicturesFragment pics = new PicturesFragment();
        pics.setArguments(bundle);
        adapter.addFragment(pics, "Photos");
        MapFragment map = new MapFragment();
        map.setArguments(bundle);
        adapter.addFragment(map, "Map");
        ReviewsFragment reviews = new ReviewsFragment();
        reviews.setArguments(bundle);
        adapter.addFragment(reviews, "Reviews");

        mViewPager.setAdapter(adapter);
    }

    private Details buildDetailsArray(String json_string){
        Gson gson = new Gson();
        return gson.fromJson(json_string, Details.class);
    }

}
