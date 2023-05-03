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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new FavoriteRecyclerAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FavoriteRecyclerAdapter.ViewHolder viewHolder, final int position) {
        JSONObject eventDetails = null;
        String EID;
        try {
            eventDetails = localDataSet.get(position);
            EID = eventDetails.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // logic for favorites
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        // check if EID exists in favorite array
        Boolean[] isFavorite = {true};
        JSONObject finalEventDetails = eventDetails;
        viewHolder.favourite.setImageResource(R.drawable.heart_filled);
        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//                viewHolder.favourite.setImageResource(R.drawable.heart_outline);
//                isFavorite[0] = !isFavorite[0];
                localDataSet.remove(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
                editor.putString("FavoriteArray", favoriteArray.toString());
                editor.apply();
                notifyDataSetChanged();
            }
        });
        try {
            viewHolder.getTextView().setText(localDataSet.get(position).getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static void registerPreferences(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.registerOnSharedPreferenceChangeListener(Callback);
    }

    public static void unregisterPreferences(Context context, SharedPreferences.OnSharedPreferenceChangeListener Callback){
        Log.d("FavoriteRecyclerAdapter", "unregisterPreferences: removed");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FavoriteList",0);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(Callback);
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView;

        public ImageView favourite;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);

            textView = (TextView) view.findViewById(R.id.eventName);
            favourite = view.findViewById(R.id.favourite);
        }

        public TextView getTextView() {
            return textView;
        }

        @Override
        public void onClick(View view) {}
    }
}
