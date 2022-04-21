package com.example.documentscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton galleryFab;
    private FloatingActionButton cameraFab;
    private FloatingActionButton pdfFab;
    private FloatingActionButton collectionFab;
    private TextView galleryText, cameraText, collectionText, pdfText;
    private Animation mFabOpen, mFabClose;
    private boolean isFabOpen = false;
    private File directory;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkAndRequestPermissions();
        galleryFab = findViewById(R.id.gallery_fab);
        pdfFab = findViewById(R.id.pdf_fab);
        cameraFab = findViewById(R.id.camera_fab);
        collectionFab = findViewById(R.id.collection_fab);
        galleryText = findViewById(R.id.gallery_text);
        pdfText = findViewById(R.id.pdf_text);
        collectionText = findViewById(R.id.collection_text);
        cameraText = findViewById(R.id.camera_text);
        mFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        directory = new File(Environment.getExternalStorageDirectory(), "ImageScanner");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    private boolean checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionOpenCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionOpenCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                String savePath = directory.getPath();
                long time = System.currentTimeMillis() / 1000;
                String imgName = "Image" + time + ".jpg";
                File imgFile = new File(savePath, imgName);
                FileOutputStream out = new FileOutputStream(imgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Intent saveIntent = new Intent(this, ListImages.class);
                saveIntent.putExtra("Clicked_Dir", savePath);
                startActivity(saveIntent);
                Toast.makeText(this, "Scan ảnh hoàn tất", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void openCamera(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);


    }
    public void openPdf(View view) {
        Intent intent = new Intent(MainActivity.this, ListPdf.class);
        startActivity(intent);
    }

    public void openCollection(View view) {
        Intent intent = new Intent(MainActivity.this, ListImages.class);
        intent.putExtra("Clicked_Dir", directory.getPath());
        startActivity(intent);
    }
    public void openGallery(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }





}
