package com.example.documentscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListPdf extends AppCompatActivity {

    private List<String> listPdf, title;
    private String dirPath;
    private RecyclerView pdfRecyclerView;
    private PdfAdapter pdfAdapter;
    private FloatingActionButton camFab, picFab, colFab;
    private TextView camText, picText, colText;
    private Animation mFabOpen, mFabClose;
    private List<String> selectedPdf;
    private boolean isOpened;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pdf);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        directory = new File(Environment.getExternalStorageDirectory(), "ImageScanner");

        pdfRecyclerView = findViewById(R.id.pdf_recyclerView);
        camFab = findViewById(R.id.pdf_camera_fab);
        picFab = findViewById(R.id.pdf_gallery_fab);
        colFab = findViewById(R.id.pdf_collection_fab);
        camText = findViewById(R.id.pdf_camera_text);
        picText = findViewById(R.id.pdf_gallery_text);
        colText = findViewById(R.id.pdf_collection_text);
        mFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        listPdf = new ArrayList<>();
        title = new ArrayList<>();
        dirPath = "/storage/emulated/0/ScannedPDF/";
        File myFile = new File(dirPath);
        if (!myFile.exists()) {
            myFile.mkdirs();
        }
        File[] list = myFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(list).length; i++) {
            listPdf.add(list[i].getAbsolutePath());
            title.add(list[i].getName());
        }
        pdfAdapter = new PdfAdapter(this, listPdf, title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        pdfRecyclerView.setLayoutManager(gridLayoutManager);
        pdfRecyclerView.setAdapter(pdfAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPdf = new ArrayList<>();
        title = new ArrayList<>();
        File myFile = new File(dirPath);
        if (!myFile.exists()) {
            myFile.mkdirs();
        }
        File[] list = myFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(list).length; i++) {
            listPdf.add(list[i].getAbsolutePath());
            title.add(list[i].getName());
        }
        pdfAdapter = new PdfAdapter(this, listPdf, title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        pdfRecyclerView.setLayoutManager(gridLayoutManager);
        pdfRecyclerView.setAdapter(pdfAdapter);
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
                Toast.makeText(this, "Scan ảnh thành công", Toast.LENGTH_LONG).show();
                String savePath = directory.getPath();
                long time = System.currentTimeMillis() / 1000;
                String imgName = "Ảnh" + time + ".jpg";
                File imgFile = new File(savePath, imgName);
                FileOutputStream out = new FileOutputStream(imgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Intent saveIntent = new Intent(this, ListImages.class);
                saveIntent.putExtra("Clicked_Dir", savePath);
                startActivity(saveIntent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deletePdf(View view) {
        selectedPdf = pdfAdapter.getSelectedPdf();
        if (selectedPdf.size() >= 1) {
            for (int i = 0; i < selectedPdf.size(); i++) {
                File picFile = new File(selectedPdf.get(i));
                picFile.delete();
            }
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            onResume();
        } else {
            Toast.makeText(this, "Chọn một hoặc nhiều file PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void sharePDF(View view) {
        selectedPdf = pdfAdapter.getSelectedPdf();
        ArrayList<Uri> uriList = new ArrayList<>();

        if (selectedPdf.size() >= 1) {
            for (int i = 0; i < selectedPdf.size(); i++)  {
                File pdfFile = new File(selectedPdf.get(i));
                Uri uri = Uri.fromFile(new File(pdfFile.getAbsolutePath()));
                uriList.add(uri);

            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            shareIntent.setType("application/pdf");
            startActivity(shareIntent);
        } else {
            Toast.makeText(this, "Vui lòng chọn một hoặc nhiều file PDF!", Toast.LENGTH_SHORT).show();
        }

    }

    public void listPdfFabAnim(View view) {
        if (isOpened) {
            camFab.startAnimation(mFabClose);
            picFab.startAnimation(mFabClose);
            colFab.startAnimation(mFabClose);
            camText.setVisibility(View.INVISIBLE);
            colText.setVisibility(View.INVISIBLE);
            picText.setVisibility(View.INVISIBLE);

            isOpened = false;
        } else {
            camFab.startAnimation(mFabOpen);
            picFab.startAnimation(mFabOpen);
            colFab.startAnimation(mFabOpen);
            camText.setVisibility(View.VISIBLE);
            colText.setVisibility(View.VISIBLE);
            picText.setVisibility(View.VISIBLE);
            isOpened = true;
        }
    }

    public void CamPdf(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void picPdf(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void collectionPdf(View view) {
        Intent intent = new Intent(ListPdf.this, ListImages.class);
        intent.putExtra("Clicked_Dir", directory.getPath());
        startActivity(intent);

    }


}
