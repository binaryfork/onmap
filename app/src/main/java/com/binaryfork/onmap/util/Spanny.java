package com.binaryfork.onmap.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * Spannable wrapper for simple creation of Spannable strings.
 */
public class Spanny {

    private SpannableStringBuilder spannable = new SpannableStringBuilder();

    public Spanny() {}

    public Spanny(String text) {
        spannable = new SpannableStringBuilder(text);
    }

    public Spanny(String text, Object span) {
        spannable = new SpannableStringBuilder(text);
        setSpan(span, 0, text.length());
    }

    public static SpannableString spanText(CharSequence text, Object span) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public Spanny setText(String text) {
        spannable = new SpannableStringBuilder(text);
        return this;
    }

    public Spanny append(String text) {
        spannable.append(text);
        return this;
    }

    public SpannableStringBuilder getSpannable() {
        return spannable;
    }

    /**
     * Sets a span to last appended string.
     *
     * @param spans Span or multiple spans.
     * @return {@code Spanny}.
     */
    public Spanny append(String text, Object... spans) {
        spannable.append(text);
        for (Object span : spans) {
            setSpan(span, spannable.length() - text.length(), spannable.length());
        }
        return this;
    }

    /**
     * Sets a span to all appearances of specified text in the spannable.
     * A new instance of a span must provided for each iteration
     * because a span can't be reused.
     *
     * @param textToSpan Case-sensitive text to span in the current spannable.
     * @param getSpan Interface to get a span for each spanned string.
     * @return {@code Spanny}.
     */
    public Spanny findAll(String textToSpan, GetSpan getSpan) {
        int lastIndex = 0;
        while(lastIndex != -1) {
            lastIndex = spannable.toString().indexOf(textToSpan, lastIndex);
            if(lastIndex != -1) {
                setSpan(getSpan.getSpan(), lastIndex, lastIndex + textToSpan.length());
                lastIndex+=textToSpan.length();
            }
        }
        return this;
    }

    /**
     * Interface to return a new span object when spanning multiple parts in the text.
     */
    public interface GetSpan {

        /**
         * @return A new span object. Never reuse a span object here, otherwise
         * only the last text part will be spanned.
         */
        Object getSpan();
    }

    private void setSpan(Object span, int start, int end) {
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}