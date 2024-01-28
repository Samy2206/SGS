package com.example.sgs;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class OpenPhotoActivity extends AppCompatActivity {
    private ImageView imgOpen;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_photo);
        imgOpen = findViewById(R.id.imgOpen);

        Bundle bundle = getIntent().getExtras();

        //imgUri = Uri.parse(bundle.getString("imgUri"));
        Glide.with(getApplicationContext())
                .load(bundle.getString("imgUri"))
                .into(imgOpen);

    }
}