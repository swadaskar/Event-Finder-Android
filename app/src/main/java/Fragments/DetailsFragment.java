package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventsearch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class DetailsFragment extends Fragment {
    private String TAG = "DetailsFragment";
    View detailView;
    JSONObject eventDetails;
    TextView artistName, venueName, date, time, genres, priceRange, ticketURL;
    Button ticketStatus;
    ImageView seatMap;
    public DetailsFragment(JSONObject json){
        eventDetails = json;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailView = inflater.inflate(R.layout.fragment_details, container, false);
        //            Log.d(TAG, String.format("Inside Details %s",eventDetails.getString("name")));

        artistName = detailView.findViewById(R.id.artistName);
        venueName = detailView.findViewById(R.id.venueName);
        date = detailView.findViewById(R.id.date);
        time = detailView.findViewById(R.id.time);
        genres = detailView.findViewById(R.id.genres);
        priceRange = detailView.findViewById(R.id.priceRange);
        ticketStatus = detailView.findViewById(R.id.ticketStatus);
        ticketURL = detailView.findViewById(R.id.ticketURL);
        seatMap = detailView.findViewById(R.id.seatMap);

        ArrayList<String> arrArtists = new ArrayList<>();
        try {
            JSONArray artists = eventDetails.getJSONObject("_embedded").getJSONArray("attractions");
            for(int i=0;i<artists.length();i++) {
                arrArtists.add(artists.getJSONObject(i).getString("name"));
            }



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Genre Field
        List<String> genreFieldList = Arrays.asList("segment", "genre", "subGenre", "type", "subType");
        List<String> filteredGenreFieldList = genreFieldList.stream()
                .filter(Type -> {
                    try {
                        return (
                                eventDetails.has("_embedded") &&
                                eventDetails.getJSONObject("_embedded").has("attractions") &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").length() > 0 &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).has("classifications") &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").length() > 0 &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).has(Type) &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).getJSONObject(Type).has("name") &&
                                eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).getJSONObject(Type).getString("name") != null &&
                                !eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).getJSONObject(Type).getString("name").equals("undefined") &&
                                !eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).getJSONObject(Type).getString("name").equals("Undefined")
                        );
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Type -> {
                    try {
                        return eventDetails.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getJSONArray("classifications").getJSONObject(0).getJSONObject(Type).getString("name");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        artistName.setText(String.join(" | ",arrArtists));
        try {
            venueName.setText(eventDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
            date.setText(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localDate"));
            time.setText(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localTime"));
            genres.setText(String.join(" | ", filteredGenreFieldList));
            if(eventDetails.has("priceRanges")){
                String priceRng = String.join("-",String.valueOf(eventDetails.getJSONArray("priceRanges").getJSONObject(0).getDouble("min")),
                        String.valueOf(eventDetails.getJSONArray("priceRanges").getJSONObject(0).getDouble("max")))+" ("
                        +eventDetails.getJSONArray("priceRanges").getJSONObject(0).getString("currency")+")";
                priceRange.setText(priceRng);
            }

            String color, ticketStatusText;
            switch (eventDetails.getJSONObject("dates").getJSONObject("status").getString("code")) {
                case "onsale":
                    color = "#00FF00";
                    ticketStatusText = "On Sale";
                case "offsale":
                    color = "#FF0000";
                    ticketStatusText = "Off Sale";
                case "canceled":
                case "cancelled":
                    color = "#000000";
                    ticketStatusText = "Canceled";
                case "postponed":
                    color = "#ffd343";
                    ticketStatusText = "Postponed";
                case "rescheduled":
                    color = "#ffd343";
                    ticketStatusText = "Rescheduled";
                default:
                    color = "#00FF00";
                    ticketStatusText = "On Sale";
            }
            ticketStatus.setBackgroundColor(Color.parseColor(color));
            ticketStatus.setText(ticketStatusText);


            ticketURL.setText(eventDetails.getString("url"));
            ticketURL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openURL = new Intent(Intent.ACTION_VIEW);
                    try {
                        openURL.setData(Uri.parse(eventDetails.getString("url")));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    detailView.getContext().startActivity(openURL);
                }
            });

            Picasso.get().load(eventDetails.getJSONObject("seatmap").getString("staticUrl")).into(seatMap);
//            String url="https://maps.ticketmaster.com/maps/geometry/3/event/2C005D0F0E090CEB/staticImage?type=png&systemId=HOST";
//            Picasso.get().load(url).into(seatMap);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return detailView;
    }
}