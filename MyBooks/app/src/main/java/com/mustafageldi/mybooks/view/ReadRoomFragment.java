package com.mustafageldi.mybooks.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.mustafageldi.mybooks.R;
import com.mustafageldi.mybooks.databinding.FragmentReadRoomBinding;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.util.Locale;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ReadRoomFragment extends Fragment {

    private FragmentReadRoomBinding binding;
    public final CompositeDisposable mDisposable = new CompositeDisposable();
    BooksDatabase database;
    BooksDao dao;
    long mTimeLeftInMillis;
    Dialog dialog;
    Dialog dialog2;
    CountDownTimer countDownTimer;
    int readPage;
    int numberOfPages;
    int id;
    NowRead nowReadRef;
    SharedPreferences sharedPreferences;
    float oldReadTime;
    float newReadTime;
    boolean which;


    public ReadRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = Room.databaseBuilder(requireContext(),BooksDatabase.class,"Books").build();
        dao = database.booksDao();
        sharedPreferences = requireContext().getSharedPreferences("com.mustafageldi.mybooks.view", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReadRoomBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        oldReadTime = sharedPreferences.getFloat("storedReadTime",0);
        if (getArguments() != null){
            int id = ReadRoomFragmentArgs.fromBundle(getArguments()).getBookid();
            mDisposable.add(dao.getSingleNowRead(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ReadRoomFragment.this :: handleResponse));
        }


        binding.destroyReadMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(),R.raw.surrender);
                mediaPlayer.start();
                binding.timeText.setText("00:00");
                binding.goReadButton.setVisibility(View.VISIBLE);
                binding.destroyReadMode.setVisibility(View.GONE);
            }
        });
    }

    public void handleResponse(NowRead nowRead){

        //DATA ACCESS
        nowReadRef = nowRead;

        Bitmap bitmap = BitmapFactory.decodeByteArray(nowRead.image,0,nowRead.image.length);
        binding.roomImageView.setImageBitmap(bitmap);

        readPage = Integer.parseInt(nowRead.readPage);
        numberOfPages = Integer.parseInt(nowRead.numberOfPages);
        id = nowRead.id;
        int progressResult = progressValuesCalculate(readPage,numberOfPages);
        binding.progressBar.setProgress(progressResult);

        binding.goReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReadMode(view);
            }
        });


    }


    private int progressValuesCalculate(int readPage, int numberOfPage){
        return readPage*100/numberOfPage;
    }


    public void goToReadMode(View view){

        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.read_mode_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button button15min = dialog.findViewById(R.id.button15min);
        Button button30min = dialog.findViewById(R.id.button30min);

        button15min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeLeftInMillis = 900000;
                which = true;
                dialog.cancel();
                getCountDownTimer();
            }
        });

        button30min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeLeftInMillis = 1800000;
                which = false;
                dialog.cancel();
                getCountDownTimer();
            }
        });

    }

    private void updateCountDownText(){
        int minutes = (int) mTimeLeftInMillis / 1000 / 60;
        int seconds = (int) mTimeLeftInMillis / 1000 % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        binding.timeText.setText(timeLeftFormatted);
    }

    private void getCountDownTimer(){

        countDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                binding.timeText.setText("00:00");
                readFinished();
            }
        }.start();
        binding.goReadButton.setVisibility(View.GONE);
        binding.destroyReadMode.setVisibility(View.VISIBLE);

    }


    private void readFinished(){

        dialog2 = new Dialog(requireContext());
        dialog2.setContentView(R.layout.read_finished_dialog);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCancelable(false);
        dialog2.show();
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(),R.raw.notification);
        mediaPlayer.start();
        binding.destroyReadMode.setVisibility(View.GONE);
        binding.goReadButton.setVisibility(View.VISIBLE);

        EditText editText = dialog2.findViewById(R.id.readingPage);

        ImageButton finishedButton = dialog2.findViewById(R.id.imageButtonFinished);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().matches("")){
                    dialog2.dismiss();
                }else {
                    int nowReadPage = Integer.parseInt(editText.getText().toString());
                    readPage = nowReadPage + readPage;
                    String newReadPage = Integer.toString(readPage);
                    dialog2.dismiss();

                    mDisposable.add(dao.updateNowRead(newReadPage, id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe());

                    if (readPage >= numberOfPages) {
                        bookIsFinished();
                    }
                }
            }
        });

        if (which){
            //15 Minutes
            newReadTime = oldReadTime + 0.25f;
            sharedPreferences.edit().putFloat("storedReadTime",newReadTime).apply();
        }else {
            //30 Minutes
            newReadTime = oldReadTime + 0.50f;
            sharedPreferences.edit().putFloat("storedReadTime",newReadTime).apply();
        }
    }


    private void bookIsFinished(){

        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(),R.raw.finished);
        mediaPlayer.start();
        Dialog dialog3 = new Dialog(requireContext());
        dialog3.setContentView(R.layout.book_is_finished_dialog);
        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog3.show();

        mDisposable.add(dao.deleteNowRead(nowReadRef)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        FinishedBooks finishedBooks = new FinishedBooks(nowReadRef.name,nowReadRef.author,nowReadRef.numberOfPages,nowReadRef.image);
        mDisposable.add(dao.insertFinishedBook(finishedBooks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        binding.progressBar.setProgress(100);
        binding.goReadButton.setText("Bu kitabı bitirdin. Tebrikler^^");
        binding.goReadButton.setEnabled(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}