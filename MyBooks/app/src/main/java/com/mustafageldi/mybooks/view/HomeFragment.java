package com.mustafageldi.mybooks.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.mustafageldi.mybooks.R;
import com.mustafageldi.mybooks.databinding.FragmentHomeBinding;



public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    float time;



    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences("com.mustafageldi.mybooks.view", Context.MODE_PRIVATE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //GET BEFORE FRAGMENT TOTAL READ PAGE OF NUMBER ARG.
        if (getArguments() != null){
            int totalReadPage = HomeFragmentArgs.fromBundle(getArguments()).getTotalReadPage();
            binding.totalPageNumberText.setText(totalReadPage + " sayfa okundu");
        }

        //GO TO ADD BOOK
        binding.addBookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_add_book(view);
            }
        });

        //GO TO NOW READ PAGE
         binding.nowReadCard.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 go_to_now_read(view);
             }
         });

         //GO TO REQUESTED LIST PAGE
         binding.requestedCard.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 go_to_requested(view);
             }
         });

         //GO TO FINISHED LIST
         binding.finishedCard.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 go_to_finished(view);
             }
         });

         binding.swankyText.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog = new Dialog(requireContext());
                 dialog.setContentView(R.layout.swanky_text_dialog);
                 dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                 dialog.show();
                 dialog.setCancelable(false);
                 MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(),R.raw.development);
                 mediaPlayer.start();
                 ImageButton button = dialog.findViewById(R.id.imageButton);
                 button.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         dialog.dismiss();
                         mediaPlayer.stop();
                     }
                 });
             }
         });
        time = sharedPreferences.getFloat("storedReadTime",0);
         binding.timeText.setText(time+" saat");
    }

    public void go_to_add_book(View view){
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToAddBookFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void go_to_now_read(View view){
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToNowReadFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void go_to_requested(View view){
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToRequestedBooks();
        Navigation.findNavController(view).navigate(action);
    }

    public void go_to_finished(View view){
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToFinishedBooksFragment();
        Navigation.findNavController(view).navigate(action);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        dialog.dismiss();
    }
}