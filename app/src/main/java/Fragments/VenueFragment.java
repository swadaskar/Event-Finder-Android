package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView venueName, address, cityState, contact;
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

        venueName = venueView.findViewById(R.id.venueName);
        address = venueView.findViewById(R.id.address);
        cityState = venueView.findViewById(R.id.cityState);
        contact = venueView.findViewById(R.id.contact);
//        genres = detailView.findViewById(R.id.genres);
//        priceRange = detailView.findViewById(R.id.priceRange);
//        ticketStatus = detailView.findViewById(R.id.ticketStatus);
//        ticketURL = detailView.findViewById(R.id.ticketURL);
//        seatMap = detailView.findViewById(R.id.seatMap);

        try {
            venueName.setText(venueInfo.getString("name"));
//            address.setText(venueInfo.getJSONObject("address").getString("line1")+", "+);
            cityState.setText(venueInfo.getString("name"));
            contact.setText(venueInfo.getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        // ** Google maps calling
        try {
            lat = venueDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("latitude");
            lon = venueDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("location").getString("longitude");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);




        return venueView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng venue = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//        LatLng venue = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions()
                .position(venue)
                .title("Venue Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venue,12f));
    }
}