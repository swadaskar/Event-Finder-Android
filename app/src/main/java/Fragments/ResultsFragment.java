package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventsearch.R;
import com.example.eventsearch.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Adapters.EventRecyclerAdapter;

public class ResultsFragment extends Fragment {
    private static final String TAG = "RootFragment";
    View resultView;
    RecyclerView recyclerView;
    JSONObject results;

    public ResultsFragment(JSONObject results){
        this.results = results;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resultView = inflater.inflate(R.layout.fragment_results, container, false);
        Utility.toastCheckHelper(resultView.getContext(),"Inside results!");
        Log.d("ResultFragment", "Inside Result Fragment");

        // code for favorites sharedpreferences
        SharedPreferences sharedPreferences = resultView.getContext().getSharedPreferences("FavoriteList",0);
        //Retrieving
        JSONArray favoriteArray = new JSONArray();
        if(sharedPreferences.contains("FavoriteArray")){
            try {
                favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray",null));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        // ** code for recyclerView
        recyclerView = resultView.findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(resultView.getContext()));
        EventRecyclerAdapter customAdapter = null;
        try {
            customAdapter = new EventRecyclerAdapter(resultView.getContext(), results, favoriteArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);

        return resultView;
    }


}