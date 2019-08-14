// https://guides.codepath.com/android/using-the-recyclerview

package com.etoitau.savedplaces;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Setting up how recyclerview works
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private List<Place> places;
    MainActivity mainActivity;

    // give reference to data being displayed
    public PlacesAdapter(MainActivity mainActivity, List<Place> places) {
        this.places = places;
        this.mainActivity = mainActivity;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View placeView = inflater.inflate(R.layout.saved_place, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(placeView);
        return viewHolder;
    }

    // Populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Get the data model based on position
        final Place place = places.get(position);

        // Set item views based on data model
        TextView nameView = holder.nameView;
        nameView.setText(place.getName());

        // add listener to delete button
        Button delete = holder.delete;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make sure it still exists
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && places.contains(place)) {
                    int pos = places.indexOf(place);
                    places.remove(pos);
                    notifyItemRemoved(pos);
                    mainActivity.savePlaces();
                }
            }
        });

        // add listener to map button
        Button map = holder.map;
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("name", place.getName());
                intent.putExtra("lat", place.getLat());
                intent.putExtra("lng", place.getLng());
                context.startActivity(intent);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return places.size();
    }

    // ViewHolder Object
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public Button delete, map;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.placeName);
            delete = itemView.findViewById(R.id.delete);
            map = itemView.findViewById(R.id.goToMap);
        }
    }
}
