package Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import Adapters.EventRecyclerAdapter;
import Adapters.FavoriteRecyclerAdapter;

public class FavoriteFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final String TAG = "FavoriteFragment";
    View favoriteView;
    RecyclerView recyclerView, eventRecyclerView;
    JSONArray favoriteArray;
    ArrayList<JSONObject> favoriteList;
    FavoriteRecyclerAdapter customAdapter;
    TextView noFavorites;
    ProgressBar loadingBarFavorite;

    @Override
    public void onStart(){
        super.onStart();
        EventRecyclerAdapter.updateWhenAdded(favoriteView.getContext(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventRecyclerAdapter.updateWhenRemoved(favoriteView.getContext(), this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favoriteView = inflater.inflate(R.layout.fragment_favorite, container, false);
        Log.d(TAG, String.format("FavoriteFragment: inside"));

        // ** code for getting sharedpreferences results
        favoriteList = new ArrayList<>();
        SharedPreferences sharedPreferences = favoriteView.getContext().getSharedPreferences("FavoriteList",0);
        favoriteArray = new JSONArray();

        if(sharedPreferences.contains("FavoriteArray")){
            try {
                favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray",null));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for(int i=favoriteArray.length()-1;i>=0;i--){
                try {
                    favoriteList.add(favoriteArray.getJSONObject(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // check if results are zero
        noFavorites = favoriteView.findViewById(R.id.noFavorites);
        noFavorites.setTextColor(Color.parseColor("#4CA327"));
        if(favoriteList.size()>0){
            noFavorites.setVisibility(View.GONE);
        }else{
            noFavorites.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, String.format("FavoriteFragment: %s",favoriteList));

        // ** code for recyclerView
        recyclerView = favoriteView.findViewById(R.id.favorites);
        eventRecyclerView = favoriteView.findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(favoriteView.getContext()));
        recyclerView.setHasFixedSize(true);
        try {
            customAdapter = new FavoriteRecyclerAdapter(favoriteView.getContext(), favoriteList, favoriteArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setVisibility(View.GONE);

        // Put loading bar on
        loadingBarFavorite = favoriteView.findViewById(R.id.loadingBarFavorite);
        loadingBarFavorite.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBarFavorite.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        },800);

        return favoriteView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String s) {
//        Log.d(TAG, String.format("onSharedPreferenceChanged: change in results %s",s));
        if(s.equals("FavoriteArray")){
            SharedPreferences sharedPreferences = favoriteView.getContext().getSharedPreferences("FavoriteList",0);
            if(sharedPreferences.contains("FavoriteArray")){
                favoriteArray = new JSONArray();
                try {
                    favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray",null));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                favoriteList = new ArrayList<>();
                for(int i=favoriteArray.length()-1;i>=0;i--){
                    try {
                        favoriteList.add(favoriteArray.getJSONObject(i));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // check if favorites are zero
            if(favoriteList.size()>0){
                noFavorites.setVisibility(View.GONE);
            }else{
                noFavorites.setVisibility(View.VISIBLE);
            }

            try {
                customAdapter = new FavoriteRecyclerAdapter(favoriteView.getContext(), favoriteList, favoriteArray);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            recyclerView.setAdapter(customAdapter);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }
}