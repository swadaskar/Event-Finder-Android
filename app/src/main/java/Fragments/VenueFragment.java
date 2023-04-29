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

import org.json.JSONException;
import org.json.JSONObject;

public class VenueFragment extends Fragment {
    private String TAG = "VenueFragment";
    View venueView;
    JSONObject venueDetails;
    public VenueFragment(JSONObject venue){
        venueDetails = venue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        venueView = inflater.inflate(R.layout.fragment_venue, container, false);

        return venueView;
    }
}