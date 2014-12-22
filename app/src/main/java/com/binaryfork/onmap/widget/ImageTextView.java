package com.binaryfork.onmap.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageTextView extends TextView implements Target {
    public ImageTextView(Context context) {
        super(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        setCompoundDrawables(d, null, null, null);
        Log.w("", "SUCCES");
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.w("", "nnn");

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
