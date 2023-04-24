package com.example.eventsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static String lat="";
    public static String lon="";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onResume() {
        super.onResume();
        setFromIpInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new SearchFragment(), "SEARCH FORM");
        vpAdapter.addFragment(new FavoriteFragment(),"FAVORITES");
        viewPager.setAdapter(vpAdapter);
    }

    public static void setFromIpInfo(){
        RequestQueue requestQueue;
        StringRequest stringRequest;
        String url = "https://ipinfo.io?token=196ec65b0b0406";
        // RequestQueue initialized
        requestQueue = Volley.newRequestQueue(this);

        // String Request initialized
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonIpinfo = new JSONObject(response);
//                    the param of String.split accept a regular expression.
                    String[] latLong = jsonIpinfo.getString("loc").split("\\,");
                    lat = latLong[0];
                    lon = latLong[1];

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Hide the Location Field
                location.getText().clear();
                location.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lat = "";
                lon = "";
                Toast.makeText(getView().this.getContext(), "Location Not Detected!", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);

    }
}