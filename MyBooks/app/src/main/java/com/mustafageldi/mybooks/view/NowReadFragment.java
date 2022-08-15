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
import com.mustafageldi.mybooks.adapter.Now_Read_Adapter;
import com.mustafageldi.mybooks.databinding.FragmentNowReadBinding;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class NowReadFragment extends Fragment {

    private FragmentNowReadBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    BooksDatabase database;
    BooksDao dao;


    public NowReadFragment() {
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
        binding = FragmentNowReadBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getData();
    }

    public void getData(){
        mDisposable.add(dao.getAllNowRead()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(NowReadFragment.this :: handleResponse));
    }

    private void handleResponse(List<NowRead> nowReadList){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        binding.recyclerView.setLayoutManager(layoutManager);
        Now_Read_Adapter now_read_adapter = new Now_Read_Adapter(nowReadList);
        binding.recyclerView.setAdapter(now_read_adapter);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }
}