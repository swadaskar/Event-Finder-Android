package Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eventsearch.R;
import com.example.eventsearch.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Adapters.EventRecyclerAdapter;
import Adapters.FavoriteRecyclerAdapter;

public class ResultsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "RootFragment";
    View resultView;
    RecyclerView recyclerView;
    JSONObject results;
    JSONArray sortedResults = new JSONArray();
    JSONArray favoriteArray;
    TextView noResults;
    ProgressBar loadingBarResults;
    LinearLayout backToSearch;
    Fragment previousSearchFragment;

    public ResultsFragment(JSONObject results, Fragment sf){
        this.results = results;
        previousSearchFragment = sf;
    }
    @Override
    public void onStart(){
        super.onStart();
        FavoriteRecyclerAdapter.updateWhenAdded(resultView.getContext(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FavoriteRecyclerAdapter.updateWhenRemoved(resultView.getContext(), this);
    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        TransitionInflater inflater = TransitionInflater.from(requireContext());
//        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resultView = inflater.inflate(R.layout.fragment_results, container, false);
        Utility.toastCheckHelper(resultView.getContext(),"Inside results!");
        Log.d("ResultFragment", "Inside Result Fragment");

        // code for back button
        backToSearch = resultView.findViewById(R.id.backToSearch);
//        backToSearch.setTextColor(Color.parseColor("#000000"));
        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // goto search fragment
//                SearchFragment searchFragment = new SearchFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.setCustomAnimations(
//                        R.anim.slide_in,  // enter
//                        R.anim.fade_out,  // exit
//                        R.anim.fade_in,   // popEnter
//                        R.anim.slide_out  // popExit
//                );
//                transaction.replace(R.id.root_frame, previousSearchFragment);
//                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
//                transaction.addToBackStack(null);       // support the Back key
//                transaction.commit();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment rf = fm.findFragmentById(R.id.root_frame);
                Fragment sf = fm.findFragmentById(R.id.search_frame);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.fade_out,  // enter
                        R.anim.fade_in   // exit
                );
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

        // check if results are zero
        noResults = resultView.findViewById(R.id.noResults);
        noResults.setTextColor(Color.parseColor("#4CA327"));

        try {
            if(results.has("_embedded") && results.getJSONObject("_embedded").has("events") && results.getJSONObject("_embedded").getJSONArray("events").length()>0){
                noResults.setVisibility(View.GONE);
                sortedResults = sortSearchResults(results);
            }else{
                noResults.setVisibility(View.VISIBLE);
//                loadingBarResults.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // ** code for recyclerView
        recyclerView = resultView.findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(resultView.getContext()));
        EventRecyclerAdapter customAdapter = null;
        try {
            customAdapter = new EventRecyclerAdapter(resultView.getContext(), sortedResults, favoriteArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(customAdapter);
        recyclerView.setVisibility(View.GONE);

        // Put loading bar on
        loadingBarResults = resultView.findViewById(R.id.loadingBarResults);
        loadingBarResults.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBarResults.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        },600);

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
                customAdapter = new EventRecyclerAdapter(resultView.getContext(), sortedResults, favoriteArray);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Put loading bar on
            loadingBarResults = resultView.findViewById(R.id.loadingBarResults);
            loadingBarResults.setVisibility(View.VISIBLE);

            // check if results are zero
            noResults = resultView.findViewById(R.id.noResults);
            noResults.setTextColor(Color.parseColor("#4CA327"));
            try {
                if(results.has("_embedded") && results.getJSONObject("_embedded").has("events") && results.getJSONObject("_embedded").getJSONArray("events").length()>0){
                    noResults.setVisibility(View.GONE);
                }else{
                    noResults.setVisibility(View.VISIBLE);
                    loadingBarResults.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            recyclerView.setAdapter(customAdapter);
            loadingBarResults.setVisibility(View.GONE);
        }
    }

    public JSONArray sortSearchResults(JSONObject searchResult) throws JSONException {
        JSONArray normalJsonArray = searchResult.getJSONObject("_embedded").getJSONArray("events");
        JSONArray sortedJsonArray = new JSONArray();

        ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();
        for (int i = 0; i < normalJsonArray.length(); i++) {
            jsonArray.add(normalJsonArray.getJSONObject(i));
        }

        Collections.sort(jsonArray, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String adate = null;
                String bdate = null;
                String atime =null;
                String btime = null;
                try {
                    adate = a.has("dates") && a.getJSONObject("dates").has("start") && a.getJSONObject("dates").getJSONObject("start").has("localDate")
                            ? a.getJSONObject("dates").getJSONObject("start").getString("localDate")
                            : "9999-12-31";
                    bdate = b.has("dates") && b.getJSONObject("dates").has("start") && b.getJSONObject("dates").getJSONObject("start").has("localDate")
                            ? b.getJSONObject("dates").getJSONObject("start").getString("localDate")
                            : "9999-12-31";

                    atime = a.has("dates") && a.getJSONObject("dates").has("start") && a.getJSONObject("dates").getJSONObject("start").has("localTime")
                            ? a.getJSONObject("dates").getJSONObject("start").getString("localTime")
                            : "23:59:59";
                    btime = b.has("dates") && b.getJSONObject("dates").has("start") && b.getJSONObject("dates").getJSONObject("start").has("localTime")
                            ? b.getJSONObject("dates").getJSONObject("start").getString("localTime")
                            : "23:59:59";
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                if (adate.compareTo(bdate) < 0) {
                    return -1; // a,b order
                }
                if (adate.compareTo(bdate) > 0) {
                    return 1; // b,a order
                } else {
                    if (LocalTime.parse(atime).compareTo(LocalTime.parse(btime)) < 0) {
                        return -1; // a,b order
                    }
                    if (LocalTime.parse(atime).compareTo(LocalTime.parse(btime)) > 0) {
                        return 1; // b,a order
                    }
                }
                return 0; // a,b maintain order
            }
        });

        for (int i = 0; i < normalJsonArray.length(); i++) {
            sortedJsonArray.put(jsonArray.get(i));
        }

        return sortedJsonArray;
    }

}