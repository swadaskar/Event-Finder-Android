package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eventsearch.R;
import com.example.eventsearch.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Adapters.EventRecyclerAdapter;
import Adapters.FavoriteRecyclerAdapter;

public class ResultsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "RootFragment";
    View resultView;
    RecyclerView recyclerView;
    JSONObject results;
    JSONArray favoriteArray;
    TextView backToSearch;
    Fragment previousSearchFragment;

    public ResultsFragment(JSONObject results, Fragment sf){
        this.results = results;
        previousSearchFragment = sf;
    }
    @Override
    public void onStart(){
        super.onStart();
        FavoriteRecyclerAdapter.registerPreferences(resultView.getContext(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FavoriteRecyclerAdapter.unregisterPreferences(resultView.getContext(), this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resultView = inflater.inflate(R.layout.fragment_results, container, false);
        Utility.toastCheckHelper(resultView.getContext(),"Inside results!");
        Log.d("ResultFragment", "Inside Result Fragment");

        // code for back button
        backToSearch = resultView.findViewById(R.id.backToSearch);
        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // goto search fragment
//                SearchFragment searchFragment = new SearchFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.root_frame, previousSearchFragment);
//                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
//                transaction.addToBackStack(null);       // support the Back key
//                transaction.commit();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment rf = fm.findFragmentById(R.id.root_frame);
                Fragment sf = fm.findFragmentById(R.id.search_frame);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(previousSearchFragment);
                transaction.remove(rf);
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.commit();
            }
        });

        // code for favorites sharedpreferences
        SharedPreferences sharedPreferences = resultView.getContext().getSharedPreferences("FavoriteList",0);
        //Retrieving
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
            customAdapter = new EventRecyclerAdapter(resultView.findViewById(R.id.mainActivityLayout), resultView.getContext(), results, favoriteArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);

        return resultView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String s) {
//        Log.d(TAG, String.format("onSharedPreferenceChanged: change in results %s",s));
        if(s.equals("FavoriteArray")){
            SharedPreferences sharedPreferences = resultView.getContext().getSharedPreferences("FavoriteList",0);
            if(sharedPreferences.contains("FavoriteArray")){
                favoriteArray = new JSONArray();
                try {
                    favoriteArray = new JSONArray(sharedPreferences.getString("FavoriteArray",null));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            EventRecyclerAdapter customAdapter = null;
            try {
                customAdapter = new EventRecyclerAdapter(resultView.findViewById(R.id.coordinatorLayoutMain), resultView.getContext(), results, favoriteArray);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            recyclerView.setAdapter(customAdapter);
        }
    }

}