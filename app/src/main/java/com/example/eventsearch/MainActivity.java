package com.example.eventsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
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

//    public static String lat="";
//    public static String lon="";

    private TabLayout tabLayout;
    private ViewPager viewPager;

//    @Override
//    protected void onResume() {
//        super.onResume();
//        setFromIpInfo();
//    }

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

//    public void setFromIpInfo(){
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://ipinfo.io?token=196ec65b0b0406";
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject ipInfo = new JSONObject(response);
//                            //                    the param of String.split accept a regular expression.
//                            String[] latLon = ipInfo.getString("loc").split(",");
//                            lat = latLon[0];
//                            lon = latLon[1];
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Context context = getApplicationContext();
//                CharSequence text = "Location Not Detected!";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }


}