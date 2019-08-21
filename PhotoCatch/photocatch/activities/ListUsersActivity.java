package com.etoitau.photocatch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etoitau.photocatch.adapters.OthersAdapter;
import com.etoitau.photocatch.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for managing the others you follow
 * All users not otherwise categorized go in Browse
 * Can put them into Collected or Burned lists
 */
public class ListUsersActivity extends AppCompatActivity {

    // Recyclerview acts differently depending on category, use three adapters
    private OthersAdapter collectedAdapter, browseAdapter, burnedAdapter;
    private ArrayList<String> others = new ArrayList<>(), // connected to recyclerview
            collectedOthers = new ArrayList<>(), // holds list of collected others
            browseOthers = new ArrayList<>(), // holds list of others to browse
            burnedOthers = new ArrayList<>(); // holds list of others burned
    private String username; // current user
    private RecyclerView rvOthers;
    final static String COLLECTED_OTHER_TABLE = "OtherCollected", // Parse object types/tables
            BURNED_OTHER_TABLE = "OtherBurned";
    // Like a tabbed view, these are the tabs
    private TextView collectedTextButton, browseTextButton, burnedTextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        // get intent, check, and unpack
        Intent intent = getIntent();
        checkIntent(intent);

        setTitle("Photo Catch - Others");

        // find some elements
        collectedTextButton = findViewById(R.id.collectedTextButton);
        browseTextButton = findViewById(R.id.browseTextButton);
        burnedTextButton = findViewById(R.id.burnedTextButton);

        // set up recyclerview and add dividing lines
        rvOthers = findViewById(R.id.recycleUsersView);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvOthers.addItemDecoration(itemDecoration);
        rvOthers.setLayoutManager(new LinearLayoutManager(this));

        // fetch collected, will get burned and browse in turn after done
        getCollectedOthers();
    }

    // set username if intent is valid
    private void checkIntent(Intent intent) {
        if (intent.getExtras() != null) {
            // has some extras, confirm has username
            if (intent.getExtras().containsKey("username")) {
                // add name to return intent
                username = intent.getStringExtra("username");
            } else {
                // should always have name, if not, something is wrong, return
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * get collected others from database
     * set up adapter for recyclerview
     */
    private void getCollectedOthers() {
        // get Others user has saved
        ParseQuery<ParseObject> q = ParseQuery.getQuery(COLLECTED_OTHER_TABLE);
        q.whereEqualTo("username", username);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object: objects) {
                        collectedOthers.add(object.getString("other"));
                    }
                } else {
                    e.printStackTrace();
                }

                others.addAll(collectedOthers);
                collectedAdapter = new OthersAdapter(ListUsersActivity.this, others, OthersAdapter.COLLECTED);
                rvOthers.setAdapter(collectedAdapter);

                // now that data is collected, user can click to view
                collectedTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setViewToCollected();
                    }
                });
                getBurnedOthers();
                setViewToCollected();
            }
        });
    }

    /**
     * get burned others from database
     * set up adapter for recyclerview
     */
    private void getBurnedOthers() {
        // get Others user has burned
        ParseQuery<ParseObject> q = ParseQuery.getQuery(BURNED_OTHER_TABLE);
        q.whereEqualTo("username", username);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object: objects) {
                        burnedOthers.add(object.getString("other"));
                    }
                } else {
                    e.printStackTrace();
                }

                burnedAdapter = new OthersAdapter(ListUsersActivity.this, others, OthersAdapter.BURNED);

                // now that data is collected, user can click to view
                burnedTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setViewToBurned();
                    }
                });
                getBrowseOthers();
            }
        });
    }

    private void getBrowseOthers() {
        ArrayList<String> usedElsewhere = new ArrayList<>();
        usedElsewhere.addAll(collectedOthers);
        usedElsewhere.addAll(burnedOthers);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", username);
        query.whereNotContainedIn("username", usedElsewhere);
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser object: objects) {
                        browseOthers.add(object.getUsername());
                    }
                } else {
                    e.printStackTrace();
                }

                browseAdapter = new OthersAdapter(ListUsersActivity.this, others, OthersAdapter.BROWSE);

                // now that data is collected, user can click to view
                browseTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setViewToBrowse();
                    }
                });
            }
        });
    }

    public void setViewToCollected() {
        collectedTextButton.setTypeface(null, Typeface.BOLD);
        browseTextButton.setTypeface(null, Typeface.NORMAL);
        burnedTextButton.setTypeface(null, Typeface.NORMAL);
        others.clear();
        others.addAll(collectedOthers);
        rvOthers.setAdapter(collectedAdapter);
    }

    public void setViewToBrowse() {
        collectedTextButton.setTypeface(null, Typeface.NORMAL);
        browseTextButton.setTypeface(null, Typeface.BOLD);
        burnedTextButton.setTypeface(null, Typeface.NORMAL);
        others.clear();
        others.addAll(browseOthers);
        rvOthers.setAdapter(browseAdapter);
    }

    public void setViewToBurned() {
        collectedTextButton.setTypeface(null, Typeface.NORMAL);
        browseTextButton.setTypeface(null, Typeface.NORMAL);
        burnedTextButton.setTypeface(null, Typeface.BOLD);
        others.clear();
        others.addAll(burnedOthers);
        rvOthers.setAdapter(burnedAdapter);
    }


    // update to show that user wants to save this other
    public void addOther(String other) {
        // update lists
        if (browseOthers.contains(other)) {
            browseOthers.remove(other);
        } else if (burnedOthers.contains(other)) {
            burnedOthers.remove(other);
        }
        collectedOthers.add(other);

        // update recycler view
        int index = others.indexOf(other);
        others.remove(index);
        rvOthers.getAdapter().notifyItemRemoved(index);

        // update databases
        // check burned
        ParseQuery<ParseObject> burnQuery = ParseQuery.getQuery(BURNED_OTHER_TABLE);
        burnQuery.whereEqualTo("username", username);
        burnQuery.whereEqualTo("other", other);
        burnQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (!objects.isEmpty()) {
                        objects.get(0).deleteInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        // add to collected
        ParseObject pObj = new ParseObject(COLLECTED_OTHER_TABLE);
        pObj.put("username", username);
        pObj.put("other", other);
        pObj.saveInBackground();
    }

    // update to show user doesn't like this other
    public void removeOther(String other) {
        // update lists
        if (browseOthers.contains(other)) {
            browseOthers.remove(other);
        } else if (collectedOthers.contains(other)) {
            collectedOthers.remove(other);
        }
        burnedOthers.add(other);

        // update recycler view
        int index = others.indexOf(other);
        others.remove(index);
        rvOthers.getAdapter().notifyItemRemoved(index);

        // update databases
        // check collected
        ParseQuery<ParseObject> collectQuery = ParseQuery.getQuery(COLLECTED_OTHER_TABLE);
        collectQuery.whereEqualTo("username", username);
        collectQuery.whereEqualTo("other", other);
        collectQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (!objects.isEmpty()) {
                        objects.get(0).deleteInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        // add to burned
        ParseObject pObj = new ParseObject(BURNED_OTHER_TABLE);
        pObj.put("username", username);
        pObj.put("other", other);
        pObj.saveInBackground();
    }

    public void clickLogout(View view) {
        MainActivity.logout();
        finish();
    }

    public void goToGallery(String galleryOf, boolean isSelf) {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("username", galleryOf);
        intent.putExtra("isSelf", isSelf);
        startActivity(intent);
    }

    public void onMeClick(View view) {
        goToGallery(username, true);
    }
}
