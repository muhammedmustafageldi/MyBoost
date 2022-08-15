package com.mustafageldi.mybooks.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mustafageldi.mybooks.databinding.FragmentSplashBinding;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;
    BooksDatabase database;
    BooksDao dao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    static int totalReadPage = 0;

    int numberPageOfFinishedBooks;
    int numberPageOfNowBooks;


    public SplashFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = Room.databaseBuilder(requireContext(),BooksDatabase.class,"Books").build();
        dao = database.booksDao();
        getReadsPageNumber();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CountDownTimer countDownTimer = new CountDownTimer(4000,1000) {
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                //NavDirections action = SplashFragmentDirections.actionSplashFragmentToHomeFragment();
                SplashFragmentDirections.ActionSplashFragmentToHomeFragment action = SplashFragmentDirections.actionSplashFragmentToHomeFragment();
                action.setTotalReadPage(numberPageOfNowBooks+numberPageOfFinishedBooks);
                Navigation.findNavController(view).navigate(action);
            }
        }.start();
    }


    private void getReadsPageNumber(){


        compositeDisposable.add(dao.getAllFinishedBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SplashFragment.this :: finishedReadPageNumber));


        compositeDisposable.add(dao.getAllNowRead()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SplashFragment.this :: nowReadPageNumber));

    }


    public void finishedReadPageNumber(List<FinishedBooks> finishedBooksList){

        numberPageOfFinishedBooks = 0;

        for (int i = 0; i<finishedBooksList.size(); i++){
            numberPageOfFinishedBooks = numberPageOfFinishedBooks + Integer.parseInt(finishedBooksList.get(i).numberOfPages);
        }


    }



    public void nowReadPageNumber(List<NowRead> nowReadList){

        numberPageOfNowBooks = 0;

        for (int i = 0; i<nowReadList.size(); i++){
            numberPageOfNowBooks = numberPageOfNowBooks + Integer.parseInt(nowReadList.get(i).readPage);
        }

        totalReadPage = totalReadPage + numberPageOfNowBooks;


    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        compositeDisposable.clear();
    }
}