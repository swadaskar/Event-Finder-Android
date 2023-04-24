package com.example.eventsearch;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    // Fragment functioning variables
    Activity activity = getActivity();
    public AutoCompleteTextView keyword;
    public ArrayAdapter<String> autoSuggestAdapter;
    public List<String> keywords = new ArrayList<String>();
    public EditText editDistance;
    public EditText editLocation;
    public Spinner spinner;
    public Switch autoDetect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        editDistance = rootView.findViewById(R.id.editDistance);
        editLocation = rootView.findViewById(R.id.editLocation);
        autoDetect = rootView.findViewById(R.id.autoDetect);


        // code for autoComplete
        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
        //Creating the instance of ArrayAdapter containing list of fruit names
        autoSuggestAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.select_dialog_item, keywords);
        //Getting the instance of AutoCompleteTextView
        keyword = (AutoCompleteTextView) rootView.findViewById(R.id.editKeyword);
        keyword.setThreshold(1);//will start working from first character
        keyword.setAdapter(autoSuggestAdapter);//setting the adapter data into the AutoCompleteTextView
        keyword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //retrieve data s
            }
            public void afterTextChanged(Editable s) {
                retrieveData(s.toString());
            }
        });

        // code for setting default Spinner
        // set default Spinner Value
        String myString = "All"; //the value you want the position for
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter myAdapter = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdapter.getPosition(myString);

        //set the default according to the value
        spinner.setSelection(spinnerPosition);

        // code for getting location
        if (!autoDetect.isChecked()){
            lat = "";
            lon = "";
//                    Make location when auto detect is unchecked
            location.setVisibility(View.VISIBLE);
        }
        else{
            MainActivity.setFromIpInfo();
        }











        // Inflate the layout for this fragment
        return rootView;
    }

    protected void retrieveData(String s){
        RequestQueue requestQueue;
        StringRequest stringRequest;
        // RequestQueue initialized
        requestQueue = Volley.newRequestQueue(this.getContext());
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

                    autoSuggestAdapter = new ArrayAdapter<String>(SearchFragment.this.getContext(), android.R.layout.select_dialog_item, items);
                    keywords = items;
                    Log.d("auto", keywords.toString());
//                    autocompleteAdapter.notifyDataSetChanged();
//                    keyword.setAdapter(autocompleteAdapter);
                    keyword.setThreshold(1);

//                    final Handler handler = new Handler(Looper.getMainLooper());

                    keyword.setAdapter(autoSuggestAdapter);
                    autoSuggestAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchFragment.this.getContext(), "No results found!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}