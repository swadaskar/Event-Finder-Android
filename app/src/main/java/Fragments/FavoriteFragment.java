package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FavoriteFragment extends Fragment {
    private final String TAG = "FavoriteFragment";
    View favoriteView;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favoriteView = inflater.inflate(R.layout.fragment_favorite, container, false);
        Log.d(TAG, String.format("FavoriteFragment: inside"));

        // ** code for getting sharedpreferences results
        ArrayList<JSONObject> favoriteList = new ArrayList<>();
        SharedPreferences sharedPreferences = favoriteView.getContext().getSharedPreferences("FavoriteList",0);
        JSONArray favoriteArray = new JSONArray();
        if(sharedPreferences.contains("FavoriteArray")){
            try {
                favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray",null));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for(int i=0;i<favoriteArray.length();i++){
                try {
                    favoriteList.add(favoriteArray.getJSONObject(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
//            noBooking.setVisibility(View.VISIBLE);
        }


        if(favoriteList.size()==0){
//            noBooking.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, String.format("FavoriteFragment: %s",favoriteList));

        // ** code for recyclerView
        recyclerView = favoriteView.findViewById(R.id.favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(favoriteView.getContext()));
        recyclerView.setHasFixedSize(true);
        FavoriteRecyclerAdapter customAdapter = null;
        try {
            customAdapter = new FavoriteRecyclerAdapter(favoriteView.getContext(), favoriteList, favoriteArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        return favoriteView;
    }
}