package com.etoitau.savedplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Show list of saved places and give option to add another or delete or view existing
 */
public class MainActivity extends AppCompatActivity {
    private ArrayList<Place> places = new ArrayList<>();
    private final int MAPS_REQUEST_CODE = 0;
    private PlacesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add dividing lines
        final RecyclerView rvPlaces = findViewById(R.id.rView);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvPlaces.addItemDecoration(itemDecoration);

        // set up recycler view
        adapter = new PlacesAdapter(places);
        rvPlaces.setAdapter(adapter);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));

        // keep to bottom of list if layout changes to accomodate keyboard
        rvPlaces.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                rvPlaces.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    // user clicks to add new view
    public void clickNewPlace(View view) {
        Log.i("called", "clickNewPlace");
        EditText nameEditText = findViewById(R.id.enterName);
        // get user's input
        String name = nameEditText.getText().toString();
        Log.i("entered:", name);
        if (name.length() < 1) {
            // if they didn't enter anything
            Toast.makeText(getApplicationContext(), "Enter name first", Toast.LENGTH_LONG).show();
            return;
        }
        // go to map to get location
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("name", name);
        startActivityForResult(intent, MAPS_REQUEST_CODE);
    }

    // when they come back from getting location at map, add to list and refresh recyclerview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAPS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // get data
                String name = data.getStringExtra("name");
                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                // add place and update recyclerview
                places.add(new Place(name, new LatLng(lat, lng)));
                adapter.notifyItemInserted(places.size() - 1);
                // clear new place text field for next
                EditText nameEditText = findViewById(R.id.enterName);
                nameEditText.setText("");
            }
        }
    }
}
