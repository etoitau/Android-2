package com.etoitau.photocatch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etoitau.photocatch.domain.Photo;
import com.etoitau.photocatch.adapters.PhotosAdapter;
import com.etoitau.photocatch.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing a user's photos
 * Can be user's own photos or others (reflected in isSelf)
 */
public class GalleryActivity extends AppCompatActivity {

    private boolean isSelf; // is user looking at own photos
    private String name; // username of photos' owner
    private static final String PICTURE_TABLE = "Photo"; // Parse object type/table name for photo storage
    private ArrayList<Photo> pics = new ArrayList<>(); // stores gallery photos
    private TextView addPhotoText, galleryTitle, messageView; // dom elements
    private PhotosAdapter photosAdapter; // recyclerview adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setTitle("Photo Catch - Gallery");

        // find DOM elements
        addPhotoText = findViewById(R.id.addPhotoText);
        galleryTitle = findViewById(R.id.galleryTitle);
        messageView = findViewById(R.id.messageView);

        // check and get data passed from ListUsersActivity
        unpackIntent();

        galleryTitle.setText(name);

        // hide add photo button if looking at someone else's photos
        if (!isSelf) {
            addPhotoText.setVisibility(View.GONE);
        }

        // set up recyclerview
        loadRecyclerView();

        // load photos from Parse server and show
        showMessage("Loading photos...");
        getPhotos();

    }

    // gives feedback to user via a textview
    private void showMessage(String message) {
        if (message.length() < 1) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setVisibility(View.VISIBLE);
        }
        messageView.setText(message);
    }

    // get info that should be provided with intent
    private void unpackIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            Log.i("Error", "no extras in intent");
            finish();
        }
        name = intent.getStringExtra("username");
        isSelf = intent.getBooleanExtra("isSelf", false);
        if (name == null) {
            Log.i("Error", "missing username");
            finish();
        }
    }

    // download photos from Parse
    private void getPhotos() {
        // find all photos asociated with this username
        ParseQuery<ParseObject> query = new ParseQuery<>(PICTURE_TABLE);
        query.whereEqualTo("username", name);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        showMessage("No photos found");
                    } else{
                        // for each ParseObject found create a Photo object,
                        // add to ArrayList, and notify RecyclerView
                        Log.i("Found photos:", Integer.toString(objects.size()));
                        for (ParseObject object: objects) {
                            final String id = object.getObjectId();
                            ParseFile file = (ParseFile) object.get("image");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        pics.add(new Photo(data, id));
                                        photosAdapter.notifyItemInserted(pics.size() - 1);
                                    } else {
                                        Log.i("Error", "getting bitmap");
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        showMessage("");
                    }
                } else {
                    Log.i("Error", "finding images");
                    e.printStackTrace();
                    showMessage("Couldn't get photos");
                }
            }
        });
    }

    // set up recyclerview with appropriate type of adapter
    private void loadRecyclerView() {
        RecyclerView rvPhotos = findViewById(R.id.recycleImageView);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        if (isSelf) {
            photosAdapter = new PhotosAdapter(GalleryActivity.this, pics, PhotosAdapter.SELF);
        } else {
            photosAdapter = new PhotosAdapter(GalleryActivity.this, pics, PhotosAdapter.OTHER);
        }
        rvPhotos.setAdapter(photosAdapter);
    }

    // return to previous activity, called when user taps "Back"
    public void goBack(View view) {
        finish();
    }

    // called when user taps Add Photo
    public void getPhotoClick(View view) {
        showMessage("");
        // actual get photo method separated so it can be called without view
        getPhoto();
    }

    // get a photo from user's device
    public void getPhoto() {
        // check/get permissions
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        // let user go get photo
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        } else {
            Log.i("Error", "no appropriate app on phone");
            showMessage("Can't find photo app");
        }

    }

    // if permission game through, try again to get photo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        } else {
            showMessage("Need permission to get photo");
        }
    }

    // when back from user selecting photo from device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // add found image to list and server
                savePhoto(bitmap);
            } catch (IOException e) {
                Log.i("Error", "Getting image from phone gallery");
                e.printStackTrace();
                showMessage("Error getting photo");
            }
        }
    }

    // add image to local list, recyclerview, and server
    private void savePhoto(Bitmap bitmap) {
        // file prep
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // start building photo object, it will still need the parse id when that's available
        final Photo photo = new Photo(byteArray);

        // set up parse object and save
        ParseFile file = new ParseFile("image.png", byteArray);
        final ParseObject obj = new ParseObject(PICTURE_TABLE);
        obj.put("image", file);
        obj.put("username", name);
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showMessage("Photo saved to server");
                // attach parse id to Photo object (needed for deletion)
                photo.setId(obj.getObjectId());
                // add to List and UI
                pics.add(photo);
                photosAdapter.notifyItemInserted(pics.size() - 1);
            }
        });
    }

    // delete a photo from UI and server
    public void deletePhoto(Photo pic) {
        if (!pics.contains(pic)) {
            Log.i("Error", "Deleting photo that doesn't exist");
            return;
        }

        // remove from List and UI
        int index = pics.indexOf(pic);
        pics.remove(pic);
        photosAdapter.notifyItemRemoved(index);

        // find on server and delete
        ParseQuery<ParseObject> q = ParseQuery.getQuery(PICTURE_TABLE);
        q.getInBackground(pic.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    object.deleteInBackground();
                    Log.i("parse", "deleting pic");
                }
            }
        });
    }
}
