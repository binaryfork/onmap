package com.binaryfork.onmap.util;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;

public class Spanny {

    private SpannableStringBuilder spannable = new SpannableStringBuilder();
    private String lastString;

    public Spanny append(String text) {
        lastString = text;
        spannable.append(text);
        return this;
    }

    public Spanny setForegroundColor(int color) {
        spannable.setSpan(
                new ForegroundColorSpan(color),
                spannable.length() - lastString.length(), spannable.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public Spanny setOppositeAlignment() {
        spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
                spannable.length() - lastString.length(), spannable.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableStringBuilder getSpannable() {
        return spannable;
    }
}
