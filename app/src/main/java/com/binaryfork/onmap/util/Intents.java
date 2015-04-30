package com.binaryfork.onmap.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Intents {

    public static void openLink(Activity activity, String link) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(link));
        activity.startActivity(newIntent);
    }

}
