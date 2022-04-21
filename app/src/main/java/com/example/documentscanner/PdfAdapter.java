package com.example.documentscanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> {


    private final List<String> pdfList;
    final List<String> title;
    private LayoutInflater inflater;
    private List<String> selectedPdf;


    public PdfAdapter(Context context, List<String> pdfList, List<String> title) {
        this.title = title;
        this.pdfList = pdfList;
        this.inflater = LayoutInflater.from(context);
        selectedPdf = new ArrayList<>();
    }

    @NonNull
    @Override
    public PdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_pdf_grid, parent, false);
        return new PdfAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PdfAdapter.ViewHolder holder, final int position) {
        File pdfFile = new File(pdfList.get(position));
        if (pdfFile.exists()) {
            holder.gridPdf.setImageResource(R.drawable.ic_picture_as_pdf_red_24dp);
            holder.pdfText.setText(pdfFile.getName());
            holder.pdfCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.pdfCheckbox.isChecked()) {
                        selectedPdf.add(pdfList.get(position));
                    } else {
                        selectedPdf.remove(pdfList.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public List<String> getSelectedPdf() {
        return selectedPdf;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pdfText;
        ImageView gridPdf;
        CheckBox pdfCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridPdf = itemView.findViewById(R.id.pdf_imageView);
            pdfText = itemView.findViewById(R.id.pdfNameText);
            pdfCheckbox = itemView.findViewById(R.id.pdfCheckbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pdfPath = pdfList.get(getAdapterPosition());
                    Intent pdfIntent = new Intent(v.getContext(), ShowPdf.class);
                    pdfIntent.putExtra("show_pdf", pdfPath);
                    v.getContext().startActivity(pdfIntent);
                }
            });
        }
    }
}
