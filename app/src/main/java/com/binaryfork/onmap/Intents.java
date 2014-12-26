package com.binaryfork.onmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Intents {

    public static void openLink(Context context, String link) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(link));
        context.startActivity(newIntent);
    }

}
