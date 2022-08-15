package com.mustafageldi.mybooks.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mustafageldi.mybooks.adapter.Requested_Books_Adapter;
import com.mustafageldi.mybooks.databinding.FragmentRequestedBooksBinding;
import com.mustafageldi.mybooks.model.RequestedBooks;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RequestedBooksFragment extends Fragment {

    private FragmentRequestedBooksBinding binding;
    BooksDatabase database;
    BooksDao dao;
    CompositeDisposable mDisposable = new CompositeDisposable();


    public RequestedBooksFragment() {
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
        binding = FragmentRequestedBooksBinding.inflate(inflater, container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }


    private void getData(){
        mDisposable.add(dao.getAllRequestedBooks()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RequestedBooksFragment.this :: handleResponse));
    }

    private void handleResponse(List<RequestedBooks> requestedBooksList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        Requested_Books_Adapter requested_books_adapter = new Requested_Books_Adapter(requestedBooksList);
        binding.recyclerView.setAdapter(requested_books_adapter);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }
}