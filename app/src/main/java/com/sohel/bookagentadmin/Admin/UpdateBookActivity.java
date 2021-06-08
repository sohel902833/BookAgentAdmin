package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.Admin.Model.ImageModel;
import com.sohel.bookagentadmin.Admin.Model.ImageModel2;
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class UpdateBookActivity extends AppCompatActivity {
    public static final int PICK_IMAGE=100;

    ImageView imageView;
    TextView booksNameTv,prevImageLTv;
    Button saveBooks,choseImage;
    TextView dateTv;
    Uri imageUri;
    ArrayList imageList = new ArrayList();
    ArrayList<ImageModel> imageArrayList=new ArrayList<>();

    //Databases
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private  boolean isImageSelected=false;
    private  int fMonth,fDay,fYear;

    private String booksId,bookName;
    private  int pDay,pMonth,pYear;
    private DatabaseReference bookRef;
    private  ProgressDialog progressBar;
    BookModel bookModel;
    String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);



        booksId=getIntent().getStringExtra("bookId");
        bookName=getIntent().getStringExtra("bookName");
        currentId=getIntent().getStringExtra("currentId");
        pDay=getIntent().getIntExtra("day",0);
        pMonth=getIntent().getIntExtra("month",0);
        pYear=getIntent().getIntExtra("year",0);


        fYear=pYear;
        fMonth=pMonth;
        fDay=pDay;
        bookRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        //initalize storage
        storageReference= FirebaseStorage.getInstance().getReference().child("BooksImage");
        //initialize database
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        //find all ui components
        imageView=findViewById(R.id.u_image_id);
        booksNameTv=findViewById(R.id.u_BookNameTextViewId);
        prevImageLTv=findViewById(R.id.u_PrevImageengthTv);

        saveBooks=findViewById(R.id.u_save_books_btn_id);
        choseImage=findViewById(R.id.u_chose_img_btn_id);
        dateTv=findViewById(R.id.u_dateTextViewid);

        booksNameTv.setText(bookName);
        dateTv.setText(pDay + "/" + pMonth+ "/" + pYear);



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
                    Toast.makeText(UpdateBookActivity.this, "Image Already Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName=booksNameTv.getText().toString();
                //save book into database
                uploadImageToStorage(bookName);

            }
        });


    }
@Override
    protected void onStart() {
        super.onStart();
        bookRef.child(booksId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("imageList")){
                                 bookModel=snapshot.getValue(BookModel.class);
                                 if(bookModel.getImageList()!=null){
                                     prevImageLTv.setText("Previous Total Image: "+bookModel.getImageList().size());
                                    progressBar.dismiss();
                                 }
                              }else{
                                progressBar.dismiss();
                            }
                        }else{
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.dismiss();
                    }
                });

    }


    private void uploadImageToStorage(String bookName) {
        if(imageList.size()>0) {
            progressBar.setTitle("Uploading Image");
            progressBar.setCancelable(false);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.setMessage("Uploaded "+0+"/"+imageList.size());
            progressBar.show();
            for (int i = 0; i < imageList.size(); i++) {
                StorageReference filePath = storageReference.child(databaseReference.push().getKey() + System.currentTimeMillis() + new Random().nextInt() + "." + getFileExtension(Uri.parse(imageList.get(i).toString())));
                int finalI = i;
                filePath.putFile(Uri.parse(imageList.get(i).toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloaduri = urlTask.getResult();

                        progressBar.setMessage("Uploaded "+(finalI+1)+"/"+imageList.size());

                        ImageModel imageModel = new ImageModel(downloaduri.toString());
                        bookModel.getImageList().add(imageModel);

                        if(finalI==imageList.size()-1){
                            saveInstanceToDatabase(bookName);
                        }

                    }
                });

            }


        }else{
            Toasty.warning(UpdateBookActivity.this, "Please Choose Image First", Toast.LENGTH_SHORT, true).show();
        }
    }
    private void saveInstanceToDatabase(String bookName) {

        progressBar.setMessage("Saving Books");

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("imageList",bookModel.getImageList());

        databaseReference
                .child(booksId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendUserToBokListActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
                Toasty.warning(UpdateBookActivity.this, "Book Upload Failed", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void sendUserToBokListActivity() {
        Intent intent=new Intent(UpdateBookActivity.this,BookListActivity.class);
        intent.putExtra("day",fDay);
        intent.putExtra("bookId",booksId);
        intent.putExtra("currentId",currentId);
        intent.putExtra("month",fMonth);
        intent.putExtra("year",fYear);
        intent.putExtra("bookName",bookName);
        startActivity(intent);
        finish();
    }


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
    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }
}