package com.mustafageldi.mybooks.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mustafageldi.mybooks.adapter.Finished_Books_Adapter;
import com.mustafageldi.mybooks.databinding.FragmentFinishedBooksBinding;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FinishedBooksFragment extends Fragment {

    private FragmentFinishedBooksBinding binding;
    BooksDatabase database;
    BooksDao dao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



    public FinishedBooksFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = Room.databaseBuilder(requireContext(),BooksDatabase.class,"Books").build();
        dao = database.booksDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinishedBooksBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void getData(){
        compositeDisposable.add(dao.getAllFinishedBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FinishedBooksFragment.this :: handleResponse));
    }


    private void handleResponse(List<FinishedBooks> finishedBooksList){

        binding.finishedSize.setText("Bitirilen kitap sayısı: "+finishedBooksList.size());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false);
        binding.recyclerView.setLayoutManager(layoutManager);
        Finished_Books_Adapter finished_books_adapter = new Finished_Books_Adapter(finishedBooksList);
        binding.recyclerView.setAdapter(finished_books_adapter);
    }


}