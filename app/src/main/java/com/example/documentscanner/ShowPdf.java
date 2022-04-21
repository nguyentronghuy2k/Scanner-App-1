package com.example.documentscanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ShowPdf extends AppCompatActivity {

    private String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PDFView pdfView = findViewById(R.id.pdfView);
        Intent myIntent = getIntent();
        Bundle bundle = myIntent.getExtras();
        pdfFileName = (String) bundle.get("show_pdf");
        Uri uri = Uri.fromFile(new File(pdfFileName));
        pdfView.fromUri(uri).load();
    }
}
