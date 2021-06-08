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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
   private  ProgressDialog progressBar;
    private Toolbar toolbar;
    DatabaseReference bookRef;

    private BookListAdapter bookListAdapter;
    private List<BookModel> bookDataList=new ArrayList<>();
    private List<BookModel> databaseBookList=new ArrayList<>();

    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        day=getIntent().getIntExtra("day",0);
        month=getIntent().getIntExtra("month",0);
        year=getIntent().getIntExtra("year",0);

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);


        bookRef=FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        toolbar=findViewById(R.id.bookList_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle(day+"/"+month+"/"+year);

        recyclerView=findViewById(R.id.bookListRecyclerViewid);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


     /*   addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToBookCreateActivity();
            }
        });*/


    }

    private void sendUserToUpdateBookActivity(String bookId,String bookName,String currentId) {
        Intent intent=new Intent(BookListActivity.this,UpdateBookActivity.class);
        intent.putExtra("bookId",bookId);
        intent.putExtra("day",day);
        intent.putExtra("currentId",currentId);
        intent.putExtra("bookName",bookName);
        intent.putExtra("month",month);
        intent.putExtra("year",year);

        startActivity(intent);
    }

    private void sendUserToBookCreateActivity(String bookId,String bookName,String currentId) {
        Intent intent=new Intent(BookListActivity.this,BooksCreateActivity.class);
        intent.putExtra("bookId",bookId);
        intent.putExtra("day",day);
        intent.putExtra("currentId",currentId);
        intent.putExtra("bookName",bookName);
        intent.putExtra("month",month);
        intent.putExtra("year",year);

        startActivity(intent);
    }
    public boolean isDatabaseExists(String currentId){
        boolean isExists=false;
        for(int i=0; i<databaseBookList.size(); i++){
            BookModel book=databaseBookList.get(i);
            if(book.getCurrentId().equals(currentId)){
                isExists=true;
                break;
            }
        }
        return  isExists;
    }

    public String getBookId(String currentId){
        String bookId="";
        for(int i=0; i<databaseBookList.size(); i++){
            BookModel book=databaseBookList.get(i);
            if(book.getCurrentId().equals(currentId)){
                bookId=book.getBookId();
                break;
            }
        }

        return bookId;
    }

    private void deleteBook(String bookId) {
        progressBar.setMessage("Deleting Book");
        progressBar.show();
        bookRef.child(bookId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(BookListActivity.this, "Book Deleted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BookListActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        prepareData();
        progressBar.show();
       bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    databaseBookList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        BookModel book=snapshot1.getValue(BookModel.class);
                        TimeDateModel time=book.getTime();
                        if(time.getDate()==day && time.getMonth()==month && time.getYear()==year) {
                            databaseBookList.add(book);
                        }
                    }
                    addImages();
                }else{
                    addImages();
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addImages() {
        for(int i=0; i<bookDataList.size(); i++){
            BookModel bBook=bookDataList.get(i);
            if(databaseBookList.size()!=0){
                for (int j = 0; j < databaseBookList.size(); j++) {
                    BookModel dBook = databaseBookList.get(j);
                    if (dBook.getCurrentId().equals(bBook.getCurrentId()) && getBookId(bBook.getCurrentId()).equals(dBook.getBookId())) {
                        if (dBook.getImageList() != null) {
                            bBook.setImageList(dBook.getImageList());
                        }
                    }
                }
            }
        }
        progressBar.dismiss();
        bookListAdapter=new BookListAdapter(this,bookDataList);
        recyclerView.setAdapter(bookListAdapter);


        bookListAdapter.setOnItemClickListner(new BookListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                BookModel currentItem=bookDataList.get(position);

                if(isDatabaseExists(currentItem.getCurrentId())){
                    if(yearIsSame(currentItem.getCurrentId())){
                        Intent intent=new Intent(BookListActivity.this,BooksReadActivity.class);
                        intent.putExtra("day",day);
                        intent.putExtra("bookId",getBookId(currentItem.getCurrentId()));
                        intent.putExtra("currentId",currentItem.getCurrentId());
                        intent.putExtra("month",month);
                        intent.putExtra("year",year);
                        intent.putExtra("bookName",currentItem.getBookName());
                        startActivity(intent);
                    }else{
                        sendUserToBookCreateActivity(currentItem.getCurrentId(),currentItem.getBookName(),currentItem.getCurrentId());
                    }
                }else{
                    sendUserToBookCreateActivity(currentItem.getCurrentId(),currentItem.getBookName(),currentItem.getCurrentId());
                }
            }

            @Override
            public void onDelete(int position) {
                BookModel book=bookDataList.get(position);
                if(isDatabaseExists(book.getCurrentId())){
                    deleteBook(getBookId(book.getCurrentId()));
                }else{
                    Toast.makeText(BookListActivity.this, "This Book Has No Child", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUpdate(int position) {
                BookModel currentItem=bookDataList.get(position);
                if(isDatabaseExists(currentItem.getCurrentId())){
                    sendUserToUpdateBookActivity(getBookId(currentItem.getCurrentId()),currentItem.getBookName(),currentItem.getCurrentId());
                }else{
                    Toast.makeText(BookListActivity.this, "This Book Has No Child", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean yearIsSame(String currentId) {
        boolean isExists=false;
        for(int i=0; i<databaseBookList.size(); i++){
            BookModel book=databaseBookList.get(i);
            TimeDateModel time=book.getTime();
            if(currentId.equals(book.getCurrentId())) {
                if (time.getDate() == day && time.getYear() == year && time.getMonth() == month) {
                    isExists = true;
                    break;
                }
            }
        }
        return  isExists;
    }
    private void prepareData() {
        BookModel book1=new BookModel("01cheechok","Cheechok");
        BookModel book2=new BookModel("02Lekchaobarn","Lekchaobarn");
        BookModel book3=new BookModel("03Leksudyod","Leksudyod");
        BookModel book4=new BookModel("04Lektipz","Lektipz");
        BookModel book5=new BookModel("05Panglek","Panglek");
        BookModel book6=new BookModel("06Pimchok","Pimchok");
        BookModel book7=new BookModel("07Ruamchai","Ruamchai");
        BookModel book8=new BookModel("08Ruamchok","Ruamchok");
        BookModel book9=new BookModel("09Ruamyod","Ruamyod");
        BookModel book10=new BookModel("10Ting","Ting");
        BookModel book11=new BookModel("11Pimjai","Pimjai");
        BookModel book12=new BookModel("12Lekmongkon ok","Lekmongkon ok");
        BookModel book13=new BookModel("13Bungearn","Bungearn");
        BookModel book14=new BookModel("14Khunjak","Khunjak");
        BookModel book15=new BookModel("15Nockchamp","Nockchamp");
        BookModel book16=new BookModel("16Nockchamp","Nockchamp");
        BookModel book17=new BookModel("17Hero","Hero");
        BookModel book18=new BookModel("18Sasima","Sasima");
        BookModel book19=new BookModel("19Kwanjai","Kwanjai");
        BookModel book20=new BookModel("20Kwanjai","Kwanjai");
        BookModel book21=new BookModel("21Juk","Juk");
        BookModel book22=new BookModel("22Laploy","Laploy");
        BookModel book23=new BookModel("2310zien","10zien");
        BookModel book24=new BookModel("24Kumpeesettee","Kumpeesettee");
        BookModel book25=new BookModel("25Daoteam","Daoteam");
        BookModel book26=new BookModel("26Huaylock","Huaylock");
        BookModel book27=new BookModel("27Tempao","Tempao");
        BookModel book28=new BookModel("28Tuagang","Tuagang");
        BookModel book29=new BookModel("29Rengbermung","Rengbermung");
        BookModel book30=new BookModel("30Kengjing","Kengjing");
        BookModel book31=new BookModel("31Ajarndung","Ajarndung");
        BookModel book32=new BookModel("32Somwang","Somwang");
        BookModel book33=new BookModel("33Lekpatihan","Lekpatihan");
        BookModel book34=new BookModel("34Tumra","Tumra");
        BookModel book35=new BookModel("35Ruaysatee","Ruaysatee");
        BookModel book36=new BookModel("36Locksumret","Locksumret");
        BookModel book37=new BookModel("37Pangleklock","Pangleklock");
        BookModel book38=new BookModel("38Kumpee","Kumpee");
        BookModel book39=new BookModel("39Tawee","Tawee");
        BookModel book40=new BookModel("40Namtip","Namtip");
        BookModel book41=new BookModel("41Huayrat","Huayrat");
        BookModel book42=new BookModel("42Pracha","Pracha");
        BookModel book43=new BookModel("43Duangmahachok","Duangmahachok");
        BookModel book44=new BookModel("44Anuntachok","Anuntachok");
        BookModel book45=new BookModel("45Songpon","Songpon");
        BookModel book46=new BookModel("46Kwanjai2","Kwanjai 2");
        BookModel book47=new BookModel("47looktung","looktung");
        BookModel book48=new BookModel("48Pakdang","Pakdang");


        bookDataList.add(book1);
        bookDataList.add(book2);
        bookDataList.add(book3);
        bookDataList.add(book4);
        bookDataList.add(book5);
        bookDataList.add(book6);
        bookDataList.add(book7);
        bookDataList.add(book8);
        bookDataList.add(book9);
        bookDataList.add(book10);
        bookDataList.add(book11);
        bookDataList.add(book12);
        bookDataList.add(book13);
        bookDataList.add(book14);
        bookDataList.add(book15);
        bookDataList.add(book16);
        bookDataList.add(book17);
        bookDataList.add(book18);
        bookDataList.add(book19);
        bookDataList.add(book20);
        bookDataList.add(book21);
        bookDataList.add(book22);
        bookDataList.add(book23);
        bookDataList.add(book24);
        bookDataList.add(book25);
        bookDataList.add(book26);
        bookDataList.add(book27);
        bookDataList.add(book28);
        bookDataList.add(book29);
        bookDataList.add(book30);
        bookDataList.add(book31);
        bookDataList.add(book32);
        bookDataList.add(book33);
        bookDataList.add(book34);
        bookDataList.add(book35);
        bookDataList.add(book36);
        bookDataList.add(book37);
        bookDataList.add(book38);
        bookDataList.add(book39);
        bookDataList.add(book40);
        bookDataList.add(book41);
        bookDataList.add(book42);
        bookDataList.add(book43);
        bookDataList.add(book44);
        bookDataList.add(book45);
        bookDataList.add(book46);
        bookDataList.add(book47);
        bookDataList.add(book48);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(BookListActivity.this,BookCategoryActivity.class);
        startActivity(intent);
        finish();
    }

}