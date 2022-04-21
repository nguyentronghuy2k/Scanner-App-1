package com.example.documentscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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

public class ListImages extends AppCompatActivity {

    private List<String> images, title;
    private String dirPath;
    private RecyclerView imgRecyclerView;
    private ImgAdapter imgAdapter;
    private FloatingActionButton camFab, picFab, pdfFab;
    private TextView camText, picText, pdfText;
    private Animation mFabOpen, mFabClose;
    private boolean isOpened;
    private List<String> selectedPics;
    private String pdfName;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_images);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        directory = new File(Environment.getExternalStorageDirectory(), "ImageScanner");
        imgRecyclerView = findViewById(R.id.img_recyclerView);
        pdfFab = findViewById(R.id.img_pdf_fab);
        camFab = findViewById(R.id.img_camera_fab);
        picFab = findViewById(R.id.img_gallery_fab);
        picText = findViewById(R.id.img_gallery_text);
        camText = findViewById(R.id.img_cam_text);
        pdfText = findViewById(R.id.img_pdf_text);
        mFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        mFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);


        Intent myIntent = getIntent();
        Bundle bundle = myIntent.getExtras();
        if (bundle != null) {
            dirPath = (String) bundle.get("Clicked_Dir");
        }
        images = new ArrayList<>();
        title = new ArrayList<>();
        File myFile = new File(directory.getPath());
        if (!myFile.exists()) {
            myFile.mkdirs();
        }
        File[] list = myFile.listFiles();
        for (int i = 0; i < list.length; i++) {
            images.add(list[i].getAbsolutePath());
            title.add(list[i].getName());
        }
        imgAdapter = new ImgAdapter(this, images, title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        imgRecyclerView.setLayoutManager(gridLayoutManager);
        imgRecyclerView.setAdapter(imgAdapter);
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
        images = new ArrayList<>();
        title = new ArrayList<>();
        File myFile = new File(dirPath);
        if (!myFile.exists()) {
            myFile.mkdirs();
        }
        File[] list = myFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(list).length; i++) {
            images.add(list[i].getAbsolutePath());
            title.add(list[i].getName());
        }
        imgAdapter = new ImgAdapter(this, images, title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        imgRecyclerView.setLayoutManager(gridLayoutManager);
        imgRecyclerView.setAdapter(imgAdapter);
    }

    public void listImgFabAnim(View view) {
        if (isOpened) {
            camFab.startAnimation(mFabClose);
            picFab.startAnimation(mFabClose);
            pdfFab.startAnimation(mFabClose);
            camText.setVisibility(View.INVISIBLE);
            picText.setVisibility(View.INVISIBLE);
            pdfText.setVisibility(View.INVISIBLE);
            isOpened = false;
        } else {
            camFab.startAnimation(mFabOpen);
            picFab.startAnimation(mFabOpen);
            pdfFab.startAnimation(mFabOpen);
            camText.setVisibility(View.VISIBLE);
            picText.setVisibility(View.VISIBLE);
            pdfText.setVisibility(View.VISIBLE);
            isOpened = true;
        }
    }

    public void PdfPhoto(View view) {
        Intent intent = new Intent(ListImages.this, ListPdf.class);
        startActivity(intent);
    }

    public void CamPhoto(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
        selectCurrentDir();
    }

    public void picPhoto(View view) {
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
        selectCurrentDir();
    }

    public void selectCurrentDir() {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        setDirPath("Dir_Path", dirPath, this);
    }

    public static void setDirPath(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void deleteImg(View view) {
        selectedPics = imgAdapter.getSelectedPics();
        if (selectedPics.size() >= 1) {
            for (int i = 0; i < selectedPics.size(); i++) {
                File picFile = new File(selectedPics.get(i));
                picFile.delete();
            }
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            onResume();
        } else {
            Toast.makeText(this, "Chọn một hoặc nhiều ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RtlHardcoded")
    public void makePDF(View view) {
        selectedPics = new ArrayList<>();
        selectedPics = imgAdapter.getSelectedPics();
        if (selectedPics.size() >= 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setWidth(100);
            editText.setGravity(Gravity.LEFT);
            alert.setMessage("Tên file PDF");
            alert.setView(editText);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pdfName = editText.getText().toString();
                    if (!pdfName.equals("")) {
                        String folderPath = "/storage/emulated/0/ScannedPDF/";
                        File pdfFolder = new File(folderPath);
                        if (!pdfFolder.exists()) {
                            pdfFolder.mkdirs();
                        }
                        final File pdfFile = new File(folderPath, pdfName + ".pdf");
                        try {
                            FileOutputStream fos = new FileOutputStream(pdfFile);
                            PdfDocument pdfDocument = new PdfDocument();
                            int pageWidth = 595;
                            int pageHeight = 842;
                            for (int i = 0; i < selectedPics.size(); i++) {
                                Bitmap bitmap = BitmapFactory.decodeFile(selectedPics.get(i));
                                if (bitmap.getWidth() > 595 | bitmap.getHeight() > 842) {
                                    bitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, pageHeight, false);
                                }
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, (i + 1)).create();
                                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                                Canvas canvas = page.getCanvas();
                                Paint paint = new Paint();
                                paint.setColor(Color.WHITE);
                                canvas.drawPaint(paint);
                                int centerX = (pageWidth - bitmap.getWidth()) / 2;
                                int centerY = (pageHeight - bitmap.getHeight()) / 2;
                                canvas.drawBitmap(bitmap, centerX, centerY, null);
                                pdfDocument.finishPage(page);
                                bitmap.recycle();
                            }
                            pdfDocument.writeTo(fos);
                            pdfDocument.close();
                            Toast.makeText(ListImages.this, "Pdf saved at Internal Storage/Doc Scanner/PDF", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ListImages.this);
                            builder.setMessage("File PDF đã được lưu !");
                            builder.setNegativeButton("OK", null);
                            builder.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ListImages.this, "Nhập tên file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.show();
        } else {
            Toast.makeText(this, "Chọn một hoặc nhiều ảnh cho việc chuyển sang PDF!", Toast.LENGTH_SHORT).show();
        }
    }

}
