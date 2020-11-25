package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    int type;
    boolean droppable;
    TaskBlock task;

    CustomTextView(Context context, int type) {
        super(context);
        this.type = type;
        droppable = true;
    }
}