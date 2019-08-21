
package com.etoitau.photocatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.etoitau.photocatch.R;
import com.etoitau.photocatch.activities.ListUsersActivity;

import java.util.List;

/**
 * Adapter for recyclerview to show Others
 * Has three modes for showing: collected others, burned others, or others not categorized (browse)
 */
public class OthersAdapter extends RecyclerView.Adapter<OthersAdapter.ViewHolder> {
    private List<String> others; // the data model
    ListUsersActivity listUsersActivity; // the calling activity

    // mode set on creation to one of three types of adapter
    private final int MODE;
    public static final int COLLECTED = 0, BROWSE = 1, BURNED = 2;

    // takes calling activity, data model, and desired display mode
    public OthersAdapter(ListUsersActivity activity, List<String> others, int mode) {
        this.others = others;
        this.listUsersActivity = activity;
        this.MODE = mode;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View otherView = inflater.inflate(R.layout.user_entry, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(otherView);
        return viewHolder;
    }

    // Populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Get the data model based on position
        final String other = others.get(position);

        // Set up add button,
        // only shows and functions if not already looking at collected others
        final TextView add = holder.otherListAdd;
        if (MODE == COLLECTED) {
            add.setVisibility(View.INVISIBLE);
        } else {
            add.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   listUsersActivity.addOther(other);
               }
            });
        }

        // set up remove button
        // only shows and functions if looking at list of others already collected
        final TextView remove = holder.otherListRemove;
        if (MODE == BURNED) {
            remove.setVisibility(View.INVISIBLE);
        } else {
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listUsersActivity.removeOther(other);
                }
            });
        }

        // set up username display
        TextView usernameView = holder.otherListUsername;
        usernameView.setText(other);

        // add listener to username to go to their gallery
        // these are all others, so isSelf is false
        usernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listUsersActivity.goToGallery(other, false);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return others.size();
    }

    // ViewHolder Object - get parts of xml item layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView otherListAdd, otherListUsername, otherListRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            this.otherListAdd = itemView.findViewById(R.id.otherListAdd);
            this.otherListUsername = itemView.findViewById(R.id.otherListUsername);
            this.otherListRemove = itemView.findViewById(R.id.otherListRemove);
        }
    }
}
