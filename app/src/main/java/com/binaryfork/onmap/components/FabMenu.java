package com.binaryfork.onmap.components;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.binaryfork.onmap.R;
import com.binaryfork.onmap.util.AndroidUtils;

import java.util.ArrayList;

public class FabMenu extends FrameLayout {

    private FloatingActionButton fab;
    private ArrayList<View> buttons = new ArrayList<>();
    private ArrayList<View> labels = new ArrayList<>();
    private boolean opened;

    public FabMenu(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FabMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FabMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    void init(Context context, AttributeSet attrs, int defStyleAttr) {
        fab = (FloatingActionButton) LayoutInflater.from(context).inflate(R.layout.fab, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(context, attrs);
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.width = AndroidUtils.dp(56);
        layoutParams.height = AndroidUtils.dp(56);
        fab.setLayoutParams(layoutParams);
        addView(fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (opened) {
                    for (int i = 0; i < buttons.size(); i++) {
                        buttons.get(i).setVisibility(GONE);
                        labels.get(i).setVisibility(GONE);
                    }
                } else {
                    for (int i = 0; i < buttons.size(); i++) {
                        buttons.get(i).setVisibility(VISIBLE);
                        labels.get(i).setVisibility(VISIBLE);
                    }
                }
                opened = !opened;
            }
        });
        addButton();
        addButton();
    }

    public void addButton() {
        FloatingActionButton button = (FloatingActionButton) LayoutInflater.from(getContext()).inflate(R.layout.fab_mini, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = AndroidUtils.dp(40);
        layoutParams.height = AndroidUtils.dp(40);
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.rightMargin = AndroidUtils.dp(16);
        layoutParams.bottomMargin = AndroidUtils.dp(80) + buttons.size() * AndroidUtils.dp(56);
        button.setLayoutParams(layoutParams);
     //   button.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.instagram_icon));//getResources().getDrawable(R.drawable.instagram_icon));
        button.setVisibility(GONE);

        TextView label = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.fab_label, null);
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        labelParams.width = AndroidUtils.dp(140);
     //   labelParams.height = AndroidUtils.dp(40);
        labelParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        labelParams.rightMargin = AndroidUtils.dp(64);
        labelParams.bottomMargin = AndroidUtils.dp(80) + buttons.size() * AndroidUtils.dp(56);
        label.setLayoutParams(labelParams);
        label.setVisibility(GONE);
        label.setText("Hello");

        buttons.add(button);
        addView(button, 0);

        labels.add(label);
        addView(label, 0);
    }
}
