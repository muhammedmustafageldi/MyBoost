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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.mustafageldi.mybooks.R;
import com.mustafageldi.mybooks.databinding.NowReadRecyclerRowBinding;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import com.mustafageldi.mybooks.view.NowReadFragmentDirections;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Now_Read_Adapter extends RecyclerView.Adapter<Now_Read_Adapter.ViewHolder>{

    public Now_Read_Adapter(List<NowRead> nowReadList) {
        this.nowReadList = nowReadList;
    }

    List<NowRead> nowReadList;






    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NowReadRecyclerRowBinding binding = NowReadRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(nowReadList.get(position).image,0,nowReadList.get(position).image.length);
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap);
        holder.binding.titleTextView.setText(nowReadList.get(position).name);
        //Progress transactions --->>>
        int progressNumber = progressCalculation(position);

        if (progressNumber == 0){
            progressNumber = 1;
            holder.binding.progressBar.setProgress(progressNumber);
            holder.binding.progressText.setText("%" + progressNumber);
            holder.binding.readPageText.setText("Tamamlanan: " + nowReadList.get(position).readPage + " sayfa");
        }else {
            holder.binding.progressBar.setProgress(progressNumber);
            holder.binding.progressText.setText("%" + progressNumber);
            holder.binding.readPageText.setText("Tamamlanan: " + nowReadList.get(position).readPage + " sayfa");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NowReadFragmentDirections.ActionNowReadFragmentToReadRoomFragment action = NowReadFragmentDirections.actionNowReadFragmentToReadRoomFragment(nowReadList.get(position).id);
                action.setBookid(nowReadList.get(position).id);
                Navigation.findNavController(view).navigate(action);
            }
        });

        //DELETE BOOK ------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>

        holder.binding.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                alertDialog.setTitle("KİTAP SİLİNSİN Mİ?");
                alertDialog.setMessage("Şu an okunanlar listesinden seçilen kitabı silmek istiyor musunuz?");
                alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BooksDatabase database = Room.databaseBuilder(holder.itemView.getContext(),BooksDatabase.class,"Books").build();
                        BooksDao dao = database.booksDao();
                        CompositeDisposable disposable = new CompositeDisposable();

                        disposable.add(dao.deleteByNowRead(nowReadList.get(position).id)
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
                                nowReadList.remove(position);
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
        return nowReadList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private NowReadRecyclerRowBinding binding;

        public ViewHolder(NowReadRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }



    public int progressCalculation(int position){

        int numberOfPage = Integer.parseInt(nowReadList.get(position).numberOfPages);
        int readPage = Integer.parseInt(nowReadList.get(position).readPage);

        return readPage*100/numberOfPage;
    }



}
