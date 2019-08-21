
package com.etoitau.photocatch.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.etoitau.photocatch.domain.Photo;
import com.etoitau.photocatch.R;
import com.etoitau.photocatch.activities.GalleryActivity;

import java.util.List;

/**
 * Adapter for recyclerview to show Photos
 * may be own photos or someone else's and works differently
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private List<Photo> pics; // the data model
    GalleryActivity galleryActivity; // the calling activity

    // mode set on creation - are these user's own photos or someone elses
    private final int MODE;
    public static final int SELF = 0, OTHER = 1;

    // takes: calling activity, data model, and desired mode
    public PhotosAdapter(GalleryActivity activity, List<Photo> pics, int mode) {
        this.pics = pics;
        this.galleryActivity = activity;
        this.MODE = mode;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.photo_entry, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(photoView);
        return viewHolder;
    }

    // Populating data into the item through holder
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Get the data model based on position
        final Photo pic = pics.get(position);

        // add pic
        final ImageView imageView = holder.imageView;
        imageView.setImageBitmap(pic.getBitmap());

        // if own photos allow deletion with a swipe
        if (MODE == SELF) {
            // add listener
            final GestureDetector gdt = new GestureDetector(new SwipeListener(pic));
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.i("onTouch", "detected touch");
                    return gdt.onTouchEvent(motionEvent);
                }
            });
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return pics.size();
    }

    // ViewHolder Object
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.photoView);
        }
    }

    // listener to detect swipe right or left to delete
    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {
        private final int MIN_DIST = 100, MIN_VELOCITY = 100;
        Photo pic;

        public SwipeListener(Photo pic) {
            super();
            this.pic = pic;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("onFling", "called");
            // if predominantly left-right, fast enough, and far enough, counts as swipe
            if (Math.abs(velocityX) > 2 * Math.abs(velocityY) &&
                    Math.abs(velocityX) > MIN_VELOCITY &&
                    Math.abs(e1.getX() - e2.getX()) > MIN_DIST) {
                Log.i("onFling", "detected fling");
                galleryActivity.deletePhoto(pic);
                return true;
            }
            return false;
        }
    }
}
