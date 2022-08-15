package com.mustafageldi.mybooks.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mustafageldi.mybooks.databinding.FinishedBooksRecyclerRowBinding;
import com.mustafageldi.mybooks.model.FinishedBooks;

import java.util.List;

public class Finished_Books_Adapter extends RecyclerView.Adapter<Finished_Books_Adapter.ViewHolder> {

    public Finished_Books_Adapter(List<FinishedBooks> finishedBooksList) {
        this.finishedBooksList = finishedBooksList;
    }

    List<FinishedBooks> finishedBooksList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FinishedBooksRecyclerRowBinding binding = FinishedBooksRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(finishedBooksList.get(position).image,0,finishedBooksList.get(position).image.length);
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap);
        holder.binding.titleTextView.setText(finishedBooksList.get(position).name);
        holder.binding.progressBar.setProgress(100);


    }

    @Override
    public int getItemCount() {
        return finishedBooksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private FinishedBooksRecyclerRowBinding binding;

        public ViewHolder(FinishedBooksRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
