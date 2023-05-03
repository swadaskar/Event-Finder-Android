package Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.R;
import com.example.eventsearch.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    // Fragment functioning variables
    private static final String TAG = "SearchFragment";
    public String latlon = "";
    public AutoCompleteTextView autokeyword;
    public ArrayAdapter<String> autoSuggestAdapter;
    public List<String> keywords = new ArrayList<String>();
    public EditText editDistance;
    public EditText editLocation;
    public Spinner spinner;
    public Switch autoDetect;
    public Button searchButton;
    public Button clearButton;
    public View rootView;
    public JSONObject searchResults;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        rootView.findViewById(R.id.keyword).setSelected(true);
        editDistance = rootView.findViewById(R.id.editDistance);
        editLocation = rootView.findViewById(R.id.editLocation);
        autoDetect = rootView.findViewById(R.id.autoDetect);
        searchButton = rootView.findViewById(R.id.searchButton);
        clearButton = rootView.findViewById(R.id.clearButton);

        // ** code for autoComplete
        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
        //Creating the instance of ArrayAdapter containing list of fruit names
        autoSuggestAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.select_dialog_item, keywords);
        //Getting the instance of AutoCompleteTextView
        autokeyword = (AutoCompleteTextView) rootView.findViewById(R.id.editKeyword);
        autokeyword.setThreshold(1);//will start working from first character
        autokeyword.setAdapter(autoSuggestAdapter);//setting the adapter data into the AutoCompleteTextView
//        autokeyword.addTextChangedListener(new TextWatcher() {
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //retrieve data s
//            }
//            public void afterTextChanged(Editable s) {
//                retrieveData(s.toString());
//            }
//        });

        // ** code for setting up Spinner
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_values, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // ** code for switch auto-detect
        autoDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autoDetect.isChecked()){
                    latlon = "";
                    editLocation.setVisibility(View.VISIBLE);
                    Utility.toastCheckHelper(rootView.getContext(),"Unchecked");
                }
                else{
                    getIpInfo();
                    editLocation.getText().clear();
                    editLocation.setVisibility(View.GONE);
                }
            }
        });

        // ** code for submit & clear button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("valid check", String.format("key: %s and loc: %s",autokeyword.getText().toString(),editLocation.getText().toString()));
                if (autokeyword.getText().toString().isEmpty()){
                    Utility.snackbarValidationHelper("Enter valid keyword!");
                } else if (!autoDetect.isChecked() && editLocation.getText().toString().isEmpty()){
                    Utility.snackbarValidationHelper("Enter valid location!");
                }else {
                    if (autoDetect.isChecked()) {
                        getSearchResults();
                    } else {
                        getGoogleCoordinates();
                    }


                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latlon="";
                autoDetect.setChecked(false);
                editLocation.setVisibility(View.VISIBLE);
                editLocation.getText().clear();
                editDistance.setText("10");
                autokeyword.setText("");
//                spinner.setSelection(spinnerPosition);
//                Log.d("After clearing", String.format("key: %s and loc: %s",autokeyword.getText().toString(),editLocation.getText().toString()));
            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }
    protected void getSearchResults() {
        String keyword = autokeyword.getText().toString();
        String distance = editDistance.getText().toString();
        String category = "default";
        switch (spinner.getSelectedItem().toString()) {
            case "All":
                category = "default";
                break;
            case "Music":
                category = "music";
                break;
            case "Sports":
                category = "sports";
                break;
            case "Arts & Theatre":
                category = "art";
                break;
            case "Film":
                category = "film";
                break;
            case "Miscellaneous":
                category = "miscellaneous";
                break;
        }
        String url = "https://api-dot-event-search-382200.uc.r.appspot.com//eventsSearch?keyword=" + keyword + "&segmentId=" + category + "&radius=" + distance + "&unit=miles&latlon=" + latlon;
//        Log.d("inputs", String.format("keyword:%s,category:%s,distance=%s,latlon=%s", keyword, category, distance, latlon));
        Log.d("input url", url);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            searchResults = new JSONObject(response);
                            Log.d("results", searchResults.toString());

                            // ** send data to result fragment
                            // goto results fragment

//                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                            transaction.replace(R.id.root_frame, resultsFragment);
//                            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
//                            transaction.addToBackStack(null);       // support the Back key
//                            transaction.commit();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
//                            Fragment rf = fm.findFragmentById(R.id.results_frame);
                            Fragment sf = fm.findFragmentById(R.id.root_frame);
                            FragmentTransaction transaction = fm.beginTransaction();
                            ResultsFragment resultsFragment = new ResultsFragment(searchResults, sf);
                            // always add new result fragment
                            transaction.add(R.id.root_frame, resultsFragment);
                            transaction.hide(sf);
                            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                            transaction.commit();
                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getSearchResults error line 178", "HTTP error: Didn't retrieve search results");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    protected void getGoogleCoordinates(){
        String location = editLocation.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+location+"&key=AIzaSyADHRIj3MAMta84N8y0ZqEnNuiAQpZKJSQ";
        Log.d("geocoding url", url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject geocoding = new JSONObject(response);
                            Log.d("geocoding status", geocoding.getString("status"));
                            if (!geocoding.getString("status").equals("OK")) {
                                Utility.toastCheckHelper(rootView.getContext(), "Enter Correct Location");
                            }
                            else {
                                latlon = geocoding.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                                        +","+geocoding.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
//                                Log.d("latlon", String.format("latlon: %s",latlon));
                                getSearchResults();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getGoogleCoordinates error line 213", "HTTP error on getting location");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    protected void retrieveData(String s){
        RequestQueue requestQueue;
        StringRequest stringRequest;
        // RequestQueue initialized
        requestQueue = Volley.newRequestQueue(rootView.getContext());
        List<String> items = new ArrayList<String>();
        String text = s.toString();
        String url = "https://api-dot-event-search-382200.uc.r.appspot.com//autoSuggest?keyword="+text;

        // String Request initialized
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject autoSuggestResponse = new JSONObject(response);
                    JSONArray results = autoSuggestResponse.getJSONObject("_embedded")
                            .getJSONArray("attractions");
                    for (int i = 0; i<results.length(); i++){
                        items.add(results.getJSONObject(i).getString("name"));
                    }

                    if (results.length()==0){
                        items.add(" ");
                    }

                    autoSuggestAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.select_dialog_item, items);
                    keywords = items;
                    Log.d("auto", keywords.toString());
//                    autocompleteAdapter.notifyDataSetChanged();
//                    keyword.setAdapter(autocompleteAdapter);
                    autokeyword.setThreshold(1);

//                    final Handler handler = new Handler(Looper.getMainLooper());

                    autokeyword.setAdapter(autoSuggestAdapter);
                    autoSuggestAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("autoSuggestError", "No results found!");
            }
        });
        requestQueue.add(stringRequest);
    }


    protected void getIpInfo(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());
        String url = "https://ipinfo.io?token=f9416a8146ee1d";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ipInfo = new JSONObject(response);
                            //                    the param of String.split accept a regular expression.
                            latlon = ipInfo.getString("loc");
                            Utility.toastCheckHelper(rootView.getContext(), String.format("Location %s",latlon));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.toastCheckHelper(rootView.getContext(), "Location Not Detected!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}