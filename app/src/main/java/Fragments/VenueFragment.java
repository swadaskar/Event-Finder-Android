package Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class VenueFragment extends Fragment implements OnMapReadyCallback {
    private String TAG = "VenueFragment";
    View venueView;
    TextView venueName, address, cityState, contact, openHoursHeading, openHoursDetail, generalRuleHeading, generalRuleDetail, childRuleHeading, childRuleDetail;
    JSONObject venueDetails;
    private String lat, lon;
    public VenueFragment(JSONObject venue){
        venueDetails = venue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        venueView = inflater.inflate(R.layout.fragment_venue, container, false);

        JSONObject venueInfo = null;
        try {
            venueInfo = venueDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // first details
        venueName = venueView.findViewById(R.id.venueName);
        address = venueView.findViewById(R.id.address);
        cityState = venueView.findViewById(R.id.cityState);
        contact = venueView.findViewById(R.id.contact);

        try {
            venueName.setText(venueInfo.getString("name"));
        } catch (JSONException e) {
            venueName.setVisibility(View.GONE);
        }
        try {
            address.setText(venueInfo.getJSONObject("address").getString("line1"));
        } catch (JSONException e) {
            address.setVisibility(View.GONE);
        }
        try {
            cityState.setText(venueInfo.getJSONObject("city").getString("name")+", "+venueInfo.getJSONObject("state").getString("name"));
        } catch (JSONException e) {
            cityState.setVisibility(View.GONE);
        }
        try {
            contact.setText(venueInfo.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail"));
        } catch (JSONException e) {
            contact.setVisibility(View.GONE);
        }

        // ** Google maps calling
        try {
            lat = venueDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude");
            lon = venueDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, String.format("lat: %s & lon: %s",lat,lon));
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Last Details
        openHoursHeading = venueView.findViewById(R.id.openHoursHeading);
        openHoursDetail = venueView.findViewById(R.id.openHoursDetail);
        generalRuleHeading = venueView.findViewById(R.id.generalRuleHeading);
        generalRuleDetail = venueView.findViewById(R.id.generalRuleDetail);
        childRuleHeading = venueView.findViewById(R.id.childRuleHeading);
        childRuleDetail = venueView.findViewById(R.id.childRuleDetail);

        try {
            openHoursDetail.setText(venueInfo.getJSONObject("boxOfficeInfo").getString("openHoursDetail"));
            setListener(openHoursDetail);
        } catch (JSONException e) {
            openHoursHeading.setVisibility(View.GONE);
            openHoursDetail.setVisibility(View.GONE);
        }
        try {
            generalRuleDetail.setText(venueInfo.getJSONObject("generalInfo").getString("generalRule"));
            setListener(generalRuleDetail);
        } catch (JSONException e) {
            generalRuleHeading.setVisibility(View.GONE);
            generalRuleDetail.setVisibility(View.GONE);
        }
        try {
            childRuleDetail.setText(venueInfo.getJSONObject("generalInfo").getString("childRule"));
            setListener(childRuleDetail);
        } catch (JSONException e) {
            childRuleHeading.setVisibility(View.GONE);
            childRuleDetail.setVisibility(View.GONE);
        }

        return venueView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG+" Inside GoogleMapCallback", String.format("lat: %s & lon: %s",Double.parseDouble(lat),Double.parseDouble(lon)));
        LatLng venue = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//        LatLng venue = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions()
                .position(venue)
                .title("Venue Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venue,12f));
    }

    protected void setListener(TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            Boolean isSmall = true;
            @Override
            public void onClick(View view) {
                if(isSmall){
                    tv.setMaxLines(10);
                }else{
                    tv.setMaxLines(3);
//                    tv.setEllipsize(tv);
                }
                isSmall = !isSmall;
            }
        });
    }
}