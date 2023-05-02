package Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.ArtistRecyclerAdapter;
import Adapters.EventRecyclerAdapter;

public class ArtistsFragment extends Fragment {
    private String TAG = "ArtistsFragment";
    RecyclerView recyclerView;
    private Context context;
    View artistsView;
    ArrayList<JSONObject> eventArtists;
    ArrayList<String> musicArtists;
    Context parentContext;

    public ArtistsFragment(ArrayList<JSONObject> artists){
//        Log.d(TAG, "Constructor run once ");
        eventArtists = artists;
//        parentContext = context;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: Inside please run once");
//        for(String artist: musicArtists){
//            getArtistDetails(artist);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        artistsView = inflater.inflate(R.layout.fragment_artists, container, false);
        Log.d(TAG, "onCreateView: Runs every time tab selected");

        // ** code for recyclerView for artists
        recyclerView = artistsView.findViewById(R.id.artists);
        recyclerView.setLayoutManager(new LinearLayoutManager(artistsView.getContext()));
        recyclerView.setHasFixedSize(true);
        ArtistRecyclerAdapter customAdapter = null;
        try {
            customAdapter = new ArtistRecyclerAdapter(artistsView.getContext(), eventArtists);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        return artistsView;
    }

}