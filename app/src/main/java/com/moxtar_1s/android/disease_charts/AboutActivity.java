package com.moxtar_1s.android.disease_charts;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.about));
        tvAbout = findViewById(R.id.tv_about);
        tvAbout.setText(getString(R.string.about_content));
    }
}
