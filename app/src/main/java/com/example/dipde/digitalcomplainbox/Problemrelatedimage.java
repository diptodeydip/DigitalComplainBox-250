package com.example.dipde.digitalcomplainbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Problemrelatedimage extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problemrelatedimage);
        iv = findViewById(R.id.problemimageview1);
        GlideApp.with(this)
                .load(MainActivity.url)
                .centerInside()
                .placeholder(R.drawable.cam)
                .into(iv);
    }
}
