package com.example.eventsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // ** setting title, getting artists JSONObject, getting venue name
        ArrayList<String> musicArtists;
        JSONObject eventDetails, eventVenue;
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

            setTitle(eventDetails.getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        FragmentManager em = getSupportFragmentManager();
        tabLayout = findViewById(R.id.eventTabLayout);
        viewPager = findViewById(R.id.eventViewPager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(em, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new DetailsFragment(eventDetails), "DETAILS");
        vpAdapter.addFragment(new ArtistsFragment(eventArtists),"ARTIST(S)");
        vpAdapter.addFragment(new VenueFragment(eventVenue),"VENUE");

        viewPager.setAdapter(vpAdapter);

    }
}