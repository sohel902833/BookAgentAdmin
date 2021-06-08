package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.Admin.Model.ImageModel;
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class BooksCreateActivity extends AppCompatActivity {
    public static final int PICK_IMAGE=100;

    ImageView imageView;
    TextView book_names_editText;
    Button saveBooks,choseImage;
    TextView dateTv;
    Uri imageUri;
    ArrayList imageList = new ArrayList();
    ArrayList<ImageModel> imageArrayList=new ArrayList<>();

    //Databases
    private  StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;
    private  boolean isImageSelected=false;
    private  int fMonth,fDay,fYear;

    private String booksId,bookName;
    private  int pDay,pMonth,pYear;
    String currentId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_create);

        // get category id from BookList Activity


        booksId=getIntent().getStringExtra("bookId");
        bookName=getIntent().getStringExtra("bookName");
        pDay=getIntent().getIntExtra("day",0);
        pMonth=getIntent().getIntExtra("month",0);
        pYear=getIntent().getIntExtra("year",0);
        currentId=getIntent().getStringExtra("currentId");


        //initalize storage
        storageReference= FirebaseStorage.getInstance().getReference().child("BooksImage");
        //initialize database
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        progressDialog=new ProgressDialog(this);
        //find all ui components
        imageView=findViewById(R.id.image_id);
        book_names_editText=findViewById(R.id.editText_id);

        saveBooks=findViewById(R.id.save_books_btn_id);
        choseImage=findViewById(R.id.chose_img_btn_id);
        dateTv=findViewById(R.id.dateTextViewid);

        book_names_editText.setText(bookName);


         fYear=pYear;
         fMonth=pMonth;
         fDay=pDay;


        dateTv.setText(pDay + "/" + pMonth+ "/" + pYear);




        //<------------------click listners----------------------->
        saveBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName=book_names_editText.getText().toString();
                    //save book into database
                    uploadImageToStorage(bookName);

            }
        });
        choseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open file manager for choose book all images
                if(!isImageSelected) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMAGE);
                }
                else{
                    Toast.makeText(BooksCreateActivity.this, "Image Already Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


      /*  dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAlarmDate();
            }
        });*/




    }
    //get file extension from uri
    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }
    private void uploadImageToStorage(String bookName) {
        imageArrayList.clear();
        if(imageList.size()>0) {

            progressDialog.setTitle("Uploading Image");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Uploaded "+0+"/"+imageList.size());
            progressDialog.show();
            for (int i = 0; i < imageList.size(); i++) {
                StorageReference filePath = storageReference.child(databaseReference.push().getKey() + System.currentTimeMillis() + new Random().nextInt() + "." + getFileExtension(Uri.parse(imageList.get(i).toString())));
                int finalI = i;
                filePath.putFile(Uri.parse(imageList.get(i).toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloaduri = urlTask.getResult();

                        progressDialog.setMessage("Uploaded "+(finalI+1)+"/"+imageList.size());

                        ImageModel imageModel = new ImageModel(downloaduri.toString());
                        imageArrayList.add(imageModel);

                        if(finalI==imageList.size()-1){
                            saveInstanceToDatabase(bookName);
                        }

                    }
                });

            }


        }else{
            Toasty.warning(BooksCreateActivity.this, "Please Choose Image First", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void saveInstanceToDatabase(String bookName) {

        progressDialog.setMessage("Saving Books");
        String currentId=databaseReference.push().getKey()+System.currentTimeMillis();

        TimeDateModel time=new TimeDateModel(fYear,fMonth,fDay);
        BookModel book=new BookModel(booksId,bookName,currentId,imageArrayList,time);
        databaseReference
                .child(currentId)
                .setValue(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendUserToBokListActivity();
                   }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.warning(BooksCreateActivity.this, "Book Upload Failed", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
    private void sendUserToBokListActivity() {
        Intent intent=new Intent(BooksCreateActivity.this,BookListActivity.class);
        intent.putExtra("day",fDay);
        intent.putExtra("bookId",booksId);
        intent.putExtra("currentId",currentId);
        intent.putExtra("month",fMonth);
        intent.putExtra("year",fYear);
        intent.putExtra("bookName",bookName);
        startActivity(intent);
        finish();
    }

    private void selectAlarmDate() {


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateTv.setText(day + "/" + (month + 1) + "/" + year);

                fYear=year;
                fMonth=month+1;
                fDay=day;

            }
        }, pYear, pMonth, pDay);
        datePickerDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSlect = 0;
                    imageList.clear();
                    while (currentImageSlect < countClipData) {
                        imageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                        imageList.add(imageUri);
                        currentImageSlect = currentImageSlect + 1;
                    }
                    imageView.setImageURI(Uri.parse(imageList.get(0).toString()));
                    isImageSelected=true;
                    choseImage.setText(imageList.size()+" Image Selected.");

                } else {
                    imageList.add(data.getData());
                    imageView.setImageURI(data.getData());
                    isImageSelected=true;
                    choseImage.setText(1+" Image Selected.");


                }
            }
        }
    }


}