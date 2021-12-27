package com.example.a2021_finalproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TravelItemView extends LinearLayout {
    public TravelItemView(Context context) {
        super(context);
        init(context);
    }

    TextView textViewplace;
    TextView textViewdate;
    TextView textViewrate;

    ImageView imageView;

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.travel_item, this, true);

        textViewplace = (TextView) findViewById(R.id.textViewplace);
        textViewdate = (TextView) findViewById(R.id.textViewdate);
        textViewrate = (TextView) findViewById(R.id.textViewrate);

        imageView = (ImageView) findViewById(R.id.imageView);
    }
}
