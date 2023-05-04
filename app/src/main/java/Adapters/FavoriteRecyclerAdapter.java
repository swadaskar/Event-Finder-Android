package Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsearch.R;
import com.example.eventsearch.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.ViewHolder>{
    private final String TAG = "FavoriteRecyclerAdapter";
    private Context context;
    private ArrayList<JSONObject> localDataSet;
    private JSONArray favoriteArray;
    public FavoriteRecyclerAdapter(Context context, ArrayList<JSONObject> dataSet, JSONArray favoriteArray) throws JSONException {
        this.context = context;
        localDataSet = dataSet;
        this.favoriteArray =favoriteArray;
        Log.d(TAG, String.format("FavoriteRecyclerAdapter: %s",localDataSet));
    }
    public static void updateWhenRemoved(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
        Log.d("FavoriteRecyclerAdapter", "updateWhenRemoved: removed");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(Callback);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_row, viewGroup, false);

        return new FavoriteRecyclerAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FavoriteRecyclerAdapter.ViewHolder viewHolder, final int position) {
        JSONObject eventDetails = null;
        String EID, eventName;
        try {
            eventDetails = localDataSet.get(position);
            EID = eventDetails.getString("id");
            eventName = eventDetails.getString("name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // logic for favorites
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        // check if EID exists in favorite array
        viewHolder.favourite.setImageResource(R.drawable.heart_filled);
        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<favoriteArray.length();i++){
                    try {
                        if(favoriteArray.getJSONObject(i).getString("id").equals(EID)){
                            favoriteArray.remove(i);
                            Utility.snackbarHelper(eventName, false, true);
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                localDataSet.remove(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
                editor.putString("FavoriteArray", favoriteArray.toString());
                editor.apply();
                notifyDataSetChanged();
            }
        });

        // Adding event data into each view-group
        try {
            Picasso.get().load(eventDetails.getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.icon);

            viewHolder.eventName.setText(eventDetails.getString("name"));
            Utility.getMarquee(viewHolder.eventName);
            viewHolder.eventDate.setText(Utility.getAmericanDate(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localDate")));

            viewHolder.venue.setText(eventDetails.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
            Utility.getMarquee(viewHolder.venue);
            viewHolder.eventTime.setText(Utility.getTwelveHoursTime(eventDetails.getJSONObject("dates").getJSONObject("start").getString("localTime")));

            viewHolder.genre.setText(eventDetails.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static void updateWhenAdded(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.registerOnSharedPreferenceChangeListener(Callback);
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
            icon = view.findViewById(R.id.iconf);
            eventName = view.findViewById(R.id.eventNamef);
            eventDate = view.findViewById(R.id.eventDatef);
            venue = view.findViewById(R.id.venuef);
            eventTime = view.findViewById(R.id.eventTimef);
            genre = view.findViewById(R.id.genref);
            favourite = view.findViewById(R.id.favouritef);
        }

        @Override
        public void onClick(View view) {}
    }
}
