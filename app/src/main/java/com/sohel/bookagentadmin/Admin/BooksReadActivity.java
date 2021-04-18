package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.ReadBookAdapter;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.Admin.Model.ImageModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class BooksReadActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ImageModel> imageDataList=new ArrayList<>();

    private Toolbar toolbar;
    private  ProgressDialog progressBar;

    String bookId,bookName;

    private DatabaseReference bookRef;
    private ReadBookAdapter readBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_read);

         bookId=getIntent().getStringExtra("bookId");
        bookName=getIntent().getStringExtra("bookName");

        toolbar=findViewById(R.id.bookRead_AppBarid);
        setSupportActionBar(toolbar);
        this.setTitle(bookName);

        bookRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();



        recyclerView=findViewById(R.id.readBookRecyclerViewId);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        readBookAdapter=new ReadBookAdapter(this,imageDataList);
        recyclerView.setAdapter(readBookAdapter);






    }


    @Override
    protected void onStart() {
        super.onStart();
        bookRef.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        imageDataList.clear();
                        if(snapshot.exists()){
                              if(snapshot.hasChild("imageList")){
                                  for(DataSnapshot snapshot1:snapshot.child("imageList").getChildren()){
                                      String imageUrl=snapshot1.child("imageUrl").getValue().toString();
                                      String id=snapshot1.getKey();

                                      ImageModel img=new ImageModel(imageUrl,id);
                                      imageDataList.add(img);
                                      readBookAdapter.notifyDataSetChanged();


                                  }
                                 progressBar.dismiss();
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







}