package com.etoitau.photocatch.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Photo object
 * holds bitmap, parse id, and can handle byte arrays
 */
public class Photo {
    private final Bitmap pic;
    private String id;

    // constructors for bitmap or byte array, id or no
    public Photo(Bitmap pic) {
        this.pic = pic;
    }

    public Photo(Bitmap pic, String id) {
        this(pic);
        this.id = id;
    }

    public Photo(byte[] data) {
        this.pic = BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public Photo(byte[] data, String id) {
        this(data);
        this.id = id;
    }

    public Bitmap getBitmap() {
        return pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }



}
