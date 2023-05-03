package Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.EventDetails;
import com.example.eventsearch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>{
    private String TAG = "EventRecyclerAdapter";
    private Context context;
    private JSONArray localDataSet;
    private JSONArray favoriteArray;
    public EventRecyclerAdapter(Context c, JSONObject dataSet, JSONArray favoriteArray) throws JSONException {
        context = c;
        localDataSet = dataSet.getJSONObject("_embedded").getJSONArray("events");
        this.favoriteArray = favoriteArray;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);
//        Log.d(TAG, "onCreateViewHolder: creating a list here will it work");
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        ;
        JSONObject eventInfo = null;
        String EID;
        try {
            eventInfo = localDataSet.getJSONObject(position);
            EID = eventInfo.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // logic for favorites
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        // check if EID exists in favorite array
        Boolean[] isFavorite = {false};
        for(int i=0;i<favoriteArray.length();i++){
            try {
                if(favoriteArray.getJSONObject(i).getString("id").equals(EID)){
                    isFavorite[0]=true;
                    break;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if(isFavorite[0]){
            viewHolder.favourite.setImageResource(R.drawable.heart_filled);
        }else{
            viewHolder.favourite.setImageResource(R.drawable.heart_outline);
        }
        JSONObject eventDetails = eventInfo;
        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite[0]){
                    for(int i=0;i<favoriteArray.length();i++){
                        try {
                            if(favoriteArray.getJSONObject(i).getString("id").equals(EID)){
                                favoriteArray.remove(i);
                                break;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    viewHolder.favourite.setImageResource(R.drawable.heart_outline);
                }else{
                    favoriteArray.put(eventDetails);
                    viewHolder.favourite.setImageResource(R.drawable.heart_filled);
                }
                isFavorite[0] = !isFavorite[0];
                editor.putString("FavoriteArray", favoriteArray.toString());
                editor.apply();
                notifyDataSetChanged();
            }
        });

        // Adding event data into each view-group
        try {
            Picasso.get().load(eventDetails.getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.icon);

            viewHolder.eventName.setText(eventDetails.getString("name"));
            viewHolder.eventName.setSelected(true);
            viewHolder.eventDate.setText(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localDate"));

            viewHolder.venue.setText(eventDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
            viewHolder.eventTime.setText(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localTime"));

            viewHolder.genre.setText(eventDetails.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length();
    }

    public static void registerPreferences(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
//        Log.d("EventRecyclerAdapter", "registerPreferences: added");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.registerOnSharedPreferenceChangeListener(Callback);
    }

    public static void unregisterPreferences(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
//        Log.d("EventRecyclerAdapter", "unregisterPreferences: removed");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(Callback);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView icon;
        public final TextView eventName,eventDate,venue,eventTime,genre;
        public final ImageView favourite;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            icon = view.findViewById(R.id.icon);
            eventName = view.findViewById(R.id.eventName);
            eventDate = view.findViewById(R.id.eventDate);
            venue = view.findViewById(R.id.venue);
            eventTime = view.findViewById(R.id.eventTime);
            genre = view.findViewById(R.id.genre);
            favourite = view.findViewById(R.id.favourite);
        }

        @Override
        public void onClick(View view) {
            Intent eventDetailIntent = new Intent(context, EventDetails.class);
//            context.startActivity(eventDetailIntent);

            int eventPosition = getAdapterPosition();
            try {
                JSONObject event = localDataSet.getJSONObject(eventPosition);
                String EID = event.getString("id");
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(view.getContext());
                String url = "https://api-dot-event-search-382200.uc.r.appspot.com/eventDetails?eventID="+EID;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONObject eventDetails = new JSONObject(response);
                                    Log.d(TAG+" details", eventDetails.toString());
                                    eventDetailIntent.putExtra("Details",eventDetails.toString());

                                    // find valid artists
                                    ArrayList<String> musicArtists = getValidArtists(eventDetails);
                                    Log.d(TAG+" musicartists", musicArtists.toString());
                                    if(musicArtists.size()!=0){
                                        eventDetailIntent.putStringArrayListExtra("Artists", musicArtists);
//                                        for(int i=0;i<musicArtists.size();i++){
//                                            boolean last = (i==musicArtists.size()-1);
//                                            getArtistDetails(eventDetailIntent, musicArtists.get(i), last, eventDetails);
//                                        }
                                        getArtistDetails(eventDetailIntent, musicArtists, 0, eventDetails);

                                    }else {
                                        eventDetailIntent.putExtra("Artists", new ArrayList<JSONObject>());
                                        getVenueDetails(eventDetailIntent, eventDetails);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error in Event Detail call ");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        protected ArrayList<String> getValidArtists(JSONObject eventDetails){
            ArrayList<String> musicArtists = new ArrayList<>();
            try {
                JSONArray eventArtists = eventDetails.getJSONObject("_embedded").getJSONArray("attractions");
                for(int i=0; i< eventArtists.length();i++){
                        if(eventArtists.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name").equals("Music")){
                            musicArtists.add(eventArtists.getJSONObject(i).getString("name"));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return musicArtists;
        }
        protected void getArtistDetails(Intent eventDetailIntent, ArrayList<String> musicArtists, int index, JSONObject eventDetails) {
            String artistName = musicArtists.get(index);
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "https://api-dot-event-search-382200.uc.r.appspot.com/artistDetails?artist="+artistName;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject artistInfo = null;
                            try {
                                artistInfo = new JSONObject(response);
                                if(artistInfo.isNull("error")){
                                    eventDetailIntent.putExtra(artistName, artistInfo.toString());
                                    Log.d(TAG+" "+artistName, artistInfo.toString());
                                }else{
                                    Log.d(TAG+" "+artistName, "onResponse: got artist error");
                                }
                                if(index==musicArtists.size()-1){
                                    getVenueDetails(eventDetailIntent, eventDetails);
                                }else {
                                    getArtistDetails(eventDetailIntent,musicArtists,index+1,eventDetails);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, String.format("Artist %s BAD response",artistName));
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
        protected void getVenueDetails(Intent eventDetailIntent, JSONObject eventDetails) throws JSONException {
            String venueName = eventDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "https://api-dot-event-search-382200.uc.r.appspot.com/venueDetails?keyword="+venueName;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject venueDetails = new JSONObject(response);
                                eventDetailIntent.putExtra("Venue", venueDetails.toString());
                                Log.d(TAG+" venue", venueDetails.toString());
                                context.startActivity(eventDetailIntent);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Venue BAD response");
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }
}
