package Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;


public class ArtistRecyclerAdapter extends RecyclerView.Adapter<ArtistRecyclerAdapter.ViewHolder> {
    private String TAG = "ArtistRecyclerAdapter";
    private Context context;
    private ArrayList<JSONObject> localDataSet;
    public ArtistRecyclerAdapter(Context context, ArrayList<JSONObject> dataSet) throws JSONException {
        this.context = context;
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArtistRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.artist_row, viewGroup, false);

        return new ArtistRecyclerAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ArtistRecyclerAdapter.ViewHolder viewHolder, final int position) {
        try {
            JSONObject artist = localDataSet.get(position);
            JSONObject artistInfo = artist.getJSONObject("artists").getJSONArray("items").getJSONObject(0);

            Picasso.get().load(artistInfo.getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.artistImage);

            viewHolder.artistName.setText(artistInfo.getString("name"));
            Utility.getMarquee(viewHolder.artistName);
            viewHolder.followers.setText(Utility.getFollowerProper(artistInfo.getJSONObject("followers").getString("total")));
            Utility.getMarquee(viewHolder.artistName);
            viewHolder.spotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openURL = new Intent(Intent.ACTION_VIEW);
                    try {
                        openURL.setData(Uri.parse(artistInfo.getJSONObject("external_urls").getString("spotify")));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    context.startActivity(openURL);
                }
            });

            viewHolder.progressBar.setProgress(artistInfo.getInt("popularity"));
            viewHolder.textViewProgress.setText(artistInfo.getString("popularity"));

            JSONArray albumImages = artist.getJSONObject("albums").getJSONArray("items");
            Picasso.get().load(albumImages.getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.albumImage1);
            Picasso.get().load(albumImages.getJSONObject(1).getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.albumImage2);
            Picasso.get().load(albumImages.getJSONObject(2).getJSONArray("images").getJSONObject(0).getString("url")).into(viewHolder.albumImage3);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView artistImage;

        public final TextView artistName;
        public final TextView followers;
        public final TextView spotify;

        public final ProgressBar progressBar;
        public final TextView textViewProgress;

        public final ImageView albumImage1, albumImage2, albumImage3;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);

            artistImage = view.findViewById(R.id.artistImage);
            artistName = view.findViewById(R.id.artistName);
            followers = view.findViewById(R.id.followers);
            spotify = view.findViewById(R.id.spotify);
            progressBar = view.findViewById(R.id.progressBar);
            textViewProgress = view.findViewById(R.id.textViewProgress);
            albumImage1 = view.findViewById(R.id.albumImage1);
            albumImage2 = view.findViewById(R.id.albumImage2);
            albumImage3 = view.findViewById(R.id.albumImage3);
        }
//        public TextView getTextView() {
//            return textView;
//        }

        @Override
        public void onClick(View view) {}
    }

}
