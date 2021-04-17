package com.sohel.bookagentadmin.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sohel.bookagentadmin.R;

public class BookListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FloatingActionButton addBookCategoryButton;

    private  ProgressDialog progressBar;

    private DatabaseReference categoryRef;
    private StorageReference storageReference;
    private Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);



        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();



        storageReference= FirebaseStorage.getInstance().getReference().child("CategoryImages");
        categoryRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("Book_Category");

        toolbar=findViewById(R.id.category_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("Books Category");

        recyclerView=findViewById(R.id.bookCategoryRecyclerViewid);
        addBookCategoryButton=findViewById(R.id.addBookCategoryButtonid);




    }
}