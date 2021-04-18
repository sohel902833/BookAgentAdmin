package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sohel.bookagentadmin.Admin.Adapter.BookCategoryAdapter;
import com.sohel.bookagentadmin.Admin.Adapter.BookListAdapter;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FloatingActionButton addBookButton;
    private  ProgressDialog progressBar;
    private Toolbar toolbar;


    String categoryId,categoryName;
    DatabaseReference bookRef;

    private BookListAdapter bookListAdapter;
    private List<BookModel> bookDataList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        categoryId=getIntent().getStringExtra("category");
        categoryName=getIntent().getStringExtra("categoryName");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);


        bookRef=FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        toolbar=findViewById(R.id.bookList_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle(categoryName);

        recyclerView=findViewById(R.id.bookListRecyclerViewid);
        addBookButton=findViewById(R.id.addBookButtonid);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        bookListAdapter=new BookListAdapter(this,bookDataList);
        recyclerView.setAdapter(bookListAdapter);


        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToBookCreateActivity();
            }
        });

        bookListAdapter.setOnItemClickListner(new BookListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                BookModel currentItem=bookDataList.get(position);
                Intent intent=new Intent(BookListActivity.this,BooksReadActivity.class);

                intent.putExtra("bookId",currentItem.getBookId());
                intent.putExtra("bookName",currentItem.getBookName());

                startActivity(intent);

            }

            @Override
            public void onDelete(int position) {

            }

            @Override
            public void onUpdate(int position) {

            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    bookDataList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        BookModel book=snapshot1.getValue(BookModel.class);
                        bookDataList.add(book);
                        bookListAdapter.notifyDataSetChanged();
                    }
                    progressBar.dismiss();

                }else{
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void sendUserToBookCreateActivity() {
        Intent intent=new Intent(BookListActivity.this,BooksCreateActivity.class);
        intent.putExtra("category",categoryId);
       startActivity(intent);
    }
}