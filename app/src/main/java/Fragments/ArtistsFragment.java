package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

public class ArtistsFragment extends Fragment {
    private String TAG = "ArtistsFragment";
    View artistsView;
    ArrayList<JSONObject> eventArtists;

    public ArtistsFragment(ArrayList<JSONObject> artists){
        eventArtists = artists;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        artistsView = inflater.inflate(R.layout.fragment_artists, container, false);


        return artistsView;
    }
}