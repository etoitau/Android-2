package com.etoitau.savedplaces;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Either shows the desired location, or allows user to get new location
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Intent intent;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("called", "maps onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("called", "onMapReady");
        mMap = googleMap;

        intent = getIntent();

        String name = "";

        // intent for returning to main activity
        final Intent returnIntent = new Intent();


        if (intent.getExtras() != null) {
            // has some extras, confirm has name
            if (intent.getExtras().containsKey("name")) {
                // add name to return intent
                name = intent.getStringExtra("name");
                returnIntent.putExtra("name", name);
            } else {
                // should always have name, if not, something is wrong, return
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }

            if (intent.getExtras().containsKey("lat")) {
                // if a latitude was given, then user want's to see a location
                LatLng latLng = new LatLng(intent.getDoubleExtra("lat", 0),
                        intent.getDoubleExtra("lng", 0));
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            } else {
                // else user is adding new location
                // show instructions
                TextView mapsMessage = findViewById(R.id.mapsMessage);
                mapsMessage.setVisibility(View.VISIBLE);
                // get user's current location and move map there
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                } else {
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager != null) {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12));
                        }
                    }
                }
                // look for long click
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        double lat = latLng.latitude;
                        double lng = latLng.longitude;
                        returnIntent.putExtra("lat", lat);
                        returnIntent.putExtra("lng", lng);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }
        } else {
            // if no extras, no good, return
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}
