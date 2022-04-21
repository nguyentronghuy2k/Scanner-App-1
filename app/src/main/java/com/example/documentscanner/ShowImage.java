package com.example.documentscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class ShowImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = findViewById(R.id.showImgView);
        Intent myIntent = getIntent();
        Bundle bundle = myIntent.getExtras();
        if (bundle != null) {
            String imgPath = (String) bundle.get("show_image");
            File imgFile = new File(imgPath);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

}
