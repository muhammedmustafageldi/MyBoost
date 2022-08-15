package com.mustafageldi.mybooks.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.mustafageldi.mybooks.R;
import com.mustafageldi.mybooks.databinding.FragmentAddBookBinding;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.model.RequestedBooks;
import com.mustafageldi.mybooks.roomdb.BooksDao;
import com.mustafageldi.mybooks.roomdb.BooksDatabase;
import java.io.ByteArrayOutputStream;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AddBookFragment extends Fragment {

    private FragmentAddBookBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> intentLauncher;
    Bitmap selectedImage;
    BooksDatabase database;
    BooksDao dao;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    int selectedSaveType;
    boolean isItEmpty;


    public AddBookFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();

        database = Room.databaseBuilder(requireContext(),BooksDatabase.class,"Books").build();
        dao = database.booksDao();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBookBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),R.array.selectList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition()==0){
                    //NOW READ
                    binding.readPage.setVisibility(View.VISIBLE);
                    binding.price.setVisibility(View.GONE);
                    binding.summary.setVisibility(View.GONE);
                    selectedSaveType = 0;

                }else if (adapterView.getSelectedItemPosition()==1){
                    //FINISHED
                    binding.readPage.setVisibility(View.GONE);
                    binding.price.setVisibility(View.GONE);
                    binding.summary.setVisibility(View.GONE);
                    selectedSaveType = 1;

                }else if (adapterView.getSelectedItemPosition()==2){
                    //Requested
                    binding.readPage.setVisibility(View.GONE);
                    binding.price.setVisibility(View.VISIBLE);
                    binding.summary.setVisibility(View.VISIBLE);
                    selectedSaveType = 2;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextsController(selectedSaveType)){
                    Toast.makeText(requireContext(), "Lütfen bütün değerleri eksiksiz girin.", Toast.LENGTH_LONG).show();
                }else {
                    save(view);
                }
            }
        });
    }



    public void save(View view){

            Bitmap smallImage = makeSmallerImage(selectedImage, 300);
            //BITMAP TO BITMAP ARRAY
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] byteArray = outputStream.toByteArray();

        String bookName = binding.bookName.getText().toString();
        String  authorName = binding.authorName.getText().toString();
        String numberOfPages = binding.numberOfPages.getText().toString();

        if (selectedSaveType == 0){
            //Now Read Save
            String readPage = binding.readPage.getText().toString();
            NowRead nowRead = new NowRead(bookName,authorName,numberOfPages,readPage,"0",byteArray);
            mDisposable.add(dao.insertNowRead(nowRead)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(AddBookFragment.this :: handleResponse));

        }else if (selectedSaveType == 1){
            //FINISHED SAVE
            FinishedBooks finishedBooks = new FinishedBooks(bookName,authorName,numberOfPages,byteArray);
            mDisposable.add(dao.insertFinishedBook(finishedBooks)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(AddBookFragment.this :: handleResponse));

        }else if (selectedSaveType == 2){
            //REQUESTED SAVE
            String summary = binding.summary.getText().toString();
            String price = binding.price.getText().toString();
            RequestedBooks requestedBooks = new RequestedBooks(bookName,authorName,numberOfPages,price,summary,byteArray);
            mDisposable.add(dao.insertRequestedBooks(requestedBooks)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(AddBookFragment.this :: handleResponse));
        }
    }


    private void handleResponse(){
        NavDirections action = AddBookFragmentDirections.actionAddBookFragmentToHomeFragment();
        Navigation.findNavController(requireView()).navigate(action);
        Toast.makeText(requireContext(),"Kayıt işlemi başarılı",Toast.LENGTH_LONG).show();
    }


    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeri için izin gerekli.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //request ok.
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentLauncher.launch(intentToGallery);
        }
    }


    public void registerLauncher(){
        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(requireContext(),"Galeri için izin gerekli!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    private boolean editTextsController(int selectedSaveType){

        if (selectedSaveType == 0) {
            if (selectedImage == null || binding.bookName.getText().toString().matches("") || binding.authorName.getText().toString().matches("") || binding.numberOfPages.getText().toString().matches("") || binding.readPage.getText().toString().matches("")) {
                isItEmpty = true;
            } else {
                isItEmpty = false;
            }
        }else if (selectedSaveType == 1){
            if (selectedImage == null || binding.bookName.getText().toString().matches("") || binding.authorName.getText().toString().matches("") || binding.numberOfPages.getText().toString().matches("")) {
                isItEmpty = true;
            } else {
                isItEmpty = false;
            }
        }else if (selectedSaveType == 2){
            if (selectedImage == null || binding.bookName.getText().toString().matches("") || binding.authorName.getText().toString().matches("") || binding.numberOfPages.getText().toString().matches("") || binding.price.getText().toString().matches("") || binding.summary.getText().toString().matches("")) {
                isItEmpty = true;
            } else {
                isItEmpty = false;
            }
        }

        return isItEmpty;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }
}