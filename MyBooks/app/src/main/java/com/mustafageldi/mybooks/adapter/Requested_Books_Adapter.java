package com.mustafageldi.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.mustafageldi.mybooks.R;
import com.mustafageldi.mybooks.databinding.RequestedBooksRecyclerRowBinding;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.model.RequestedBooks;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Requested_Books_Adapter extends RecyclerView.Adapter<Requested_Books_Adapter.ViewHolder> {

    public Requested_Books_Adapter(List<RequestedBooks> requestedBooksList) {
        this.requestedBooksList = requestedBooksList;
    }


    List<RequestedBooks> requestedBooksList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RequestedBooksRecyclerRowBinding binding = RequestedBooksRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(requestedBooksList.get(position).image,0,requestedBooksList.get(position).image.length);
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap);
        holder.binding.titleTextView.setText(requestedBooksList.get(position).name);
        holder.binding.recyclerSummaryText.setText(requestedBooksList.get(position).summary);
        holder.binding.recyclerPriceText.setText("Fiyatı: "+requestedBooksList.get(position).price);

        holder.binding.obtainBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferBook(holder.itemView.getContext(), requestedBooksList.get(position).id,requestedBooksList.get(position).name,requestedBooksList.get(position).author,requestedBooksList.get(position).numberOfPages,requestedBooksList.get(position).image);

                Dialog dialog = new Dialog(holder.itemView.getContext());
                dialog.setContentView(R.layout.obtain_book_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();

                Button button = dialog.findViewById(R.id.buttonOk);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        notifyDataSetChanged();
                        requestedBooksList.remove(position);
                        notifyItemRemoved(position);
                    }});}});

        holder.binding.deleteImageRequested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                alertDialog.setTitle("KİTAP SİLİNSİN Mİ?");
                alertDialog.setMessage("İstenen kitaplar listesinden seçilen kitabı silmek istiyor musunuz?");
                alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BooksDatabase database = Room.databaseBuilder(holder.itemView.getContext(),BooksDatabase.class,"Books").build();
                        BooksDao dao = database.booksDao();
                        CompositeDisposable disposable = new CompositeDisposable();

                        disposable.add(dao.deleteByIdRequested(requestedBooksList.get(position).id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe());

                        Dialog dialog = new Dialog(holder.itemView.getContext());
                        dialog.setContentView(R.layout.delete_dialog);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        Button button = dialog.findViewById(R.id.buttonOkDelete);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                notifyDataSetChanged();
                                requestedBooksList.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
                    }
                }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestedBooksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private RequestedBooksRecyclerRowBinding binding;

        public ViewHolder(RequestedBooksRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    private void transferBook(Context requireContext, int id, String bookName, String author, String numberOfPages, byte[] image){

        BooksDatabase database = Room.databaseBuilder(requireContext, BooksDatabase.class,"Books").build();
        BooksDao dao = database.booksDao();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(dao.deleteByIdRequested(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        NowRead nowRead = new NowRead(bookName,author,numberOfPages,"0","0",image);
        compositeDisposable.add(dao.insertNowRead(nowRead)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

}
