package com.example.eventsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.VPAdapter;
import Fragments.ArtistsFragment;
import Fragments.DetailsFragment;
import Fragments.VenueFragment;

public class EventDetails extends AppCompatActivity {

    TextView eventName;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Boolean isFavorite=false;
    private Context context;
    private JSONArray favoriteArray;
    private JSONObject eventDetails;
    private Menu mainMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EventSearch);
        setContentView(R.layout.activity_event_details);


        // ** setting title, getting artists JSONObject, getting venue name
        ArrayList<String> musicArtists;
        JSONObject eventVenue;
        ArrayList<JSONObject> eventArtists = new ArrayList<>();
        Intent intent = getIntent();
        try {
            eventDetails = new JSONObject(intent.getStringExtra("Details"));

            musicArtists = new ArrayList<>(intent.getStringArrayListExtra("Artists"));
            for(String artist: musicArtists){
                if (intent.hasExtra(artist)){
                    eventArtists.add(new JSONObject(intent.getStringExtra(artist)));
                }
            }

        eventVenue = new JSONObject(intent.getStringExtra("Venue"));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // check if event is favorite or not
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("FavoriteList",0);
        favoriteArray = new JSONArray();
        if(sharedPreferences.contains("FavoriteArray")) {
            try {
                favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray", null));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        for(int i=0;i<favoriteArray.length();i++){
            try {
                if(favoriteArray.getJSONObject(i).getString("id").equals(eventDetails.getString("id"))){
                    isFavorite=true;
                    break;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }



        // ActionBar code
        ActionBar actionBar = getSupportActionBar();
        try {
            setTitle(Html.fromHtml("<font color=\"#4CA327\">" + eventDetails.getString("name") + "</font>"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        actionBar.setHomeAsUpIndicator(R.drawable.green_back_btn);
        actionBar.setDisplayHomeAsUpEnabled(true);




        FragmentManager em = getSupportFragmentManager();
        tabLayout = findViewById(R.id.eventTabLayout);
        viewPager = findViewById(R.id.eventViewPager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(em, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new DetailsFragment(eventDetails), "DETAILS");
        vpAdapter.addFragment(new ArtistsFragment(eventArtists),"ARTIST(S)");
        vpAdapter.addFragment(new VenueFragment(eventVenue),"VENUE");

        viewPager.setAdapter(vpAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.info_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.artist_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.venue_icon);
//        tabLayout.getTabAt(0).setColorFilter

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mainMenu = menu;
        inflater.inflate(R.menu.my_options_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    // this event will enable the back
    // function to the button on press
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.facebook:
                // User chose the "Facebook" item, show the app settings UI...
                // method to redirect to provided link
                Intent facebookIntent = null;
                try {
                    facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/sharer/sharer.php?u="+eventDetails.getString("url")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                startActivity(facebookIntent);
                return true;

            case R.id.twitter:
                // User chose the "Twitter" action, mark the current item
                Intent twitterIntent = null;
                try {
                    twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet/?text=Check "+eventDetails.getString("name")+" on Ticketmaster.%0A"+eventDetails.getString("url")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                startActivity(twitterIntent);
                return true;

            case R.id.favoriteMenu:
                // User chose the "Twitter" action, mark the current item
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("FavoriteList",0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(isFavorite){
                    for(int i=0;i<favoriteArray.length();i++){
                        try {
                            if(favoriteArray.getJSONObject(i).getString("id").equals(eventDetails.getString("id"))){
                                favoriteArray.remove(i);
                                break;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    item.setIcon(R.drawable.heart_outline);
                }else{
                    favoriteArray.put(eventDetails);
                    item.setIcon(R.drawable.heart_filled);
                }
                isFavorite = !isFavorite;
                editor.putString("FavoriteArray", favoriteArray.toString());
                editor.apply();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        // Set favorite menu item
        MenuItem item3 = menu.getItem(2);
        item3.setIcon(isFavorite?R.drawable.heart_filled:R.drawable.heart_outline);
        return super.onPrepareOptionsMenu(menu);
    }
}