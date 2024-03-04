package com.e_passport.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public class Card {
    private Bitmap imgBmp;
    private String title;
    private String id;
    public Card(Context context, String id, String title, String imgSrc) throws IOException {
        this.id = id;
        this.title = title;
        this.imgBmp = BitmapFactory.decodeStream(context.getAssets().open("imgs/" + imgSrc));
    }
    public String getId() {
        return id;
    }
    public Bitmap getImgBmp() {
        return imgBmp;
    }

    public String getTitle() {
        return title;
    }
}
