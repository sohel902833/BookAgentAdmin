package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.Adapter.BookCategoryAdapter;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BookCategoryActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FloatingActionButton addBookCategoryButton;


    //for diolouge vaiable
    ImageView categoryImageView;
    private Uri imageUri;
    private ProgressDialog progressBar;

    private Button searchButton;

    //Firebase
    private DatabaseReference categoryRef,bookRef;
    private StorageReference storageReference;

    private BookCategoryAdapter categoryAdapter;
    private Toolbar toolbar;
    private List<TimeDateModel> dataList=new ArrayList<>();
    private List<TimeDateModel> yearsList=new ArrayList<>();

    private Spinner yearSelectSpinner;
    ArrayAdapter spinnerAdapter;
    boolean oneTime=false;
    int cYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_category);

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);


        Calendar calendar = Calendar.getInstance();
        cYear = calendar.get(Calendar.YEAR);

        storageReference= FirebaseStorage.getInstance().getReference().child("CategoryImages");
        bookRef=FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");


        toolbar=findViewById(R.id.category_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("Books Category");


        recyclerView=findViewById(R.id.bookCategoryRecyclerViewid);
        yearSelectSpinner=findViewById(R.id.yearSelectId);
        searchButton=findViewById(R.id.searchButton);





        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

      /*  spinnerAdapter=new ArrayAdapter(BookCategoryActivity.this,R.layout.item_layout,R.id.spinnerHeaderTExt,uniqueYears);
        yearSelectSpinner.setAdapter(spinnerAdapter);
*/



        setRecyclerView(cYear);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem=yearSelectSpinner.getSelectedItem().toString();
                setRecyclerView(Integer.parseInt(selectedItem));
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.show();
        prepareData();
        loadData();
    }

    public void setRecyclerView(int year){
        categoryAdapter=new BookCategoryAdapter(this,dataList,year);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
    }


  public void  prepareData(){
        dataList.clear();

        for(int i=1; i<=12; i++){
            TimeDateModel time=new TimeDateModel(i,16);
            dataList.add(time);
        }
        for(int i=2; i<=12; i++){
            TimeDateModel time=new TimeDateModel(i,1);
            dataList.add(time);
        }

        TimeDateModel time2=new TimeDateModel(12,30);
        dataList.add(time2);
        categoryAdapter.notifyDataSetChanged();


    }
    private void sendUserToBookCreateActivity() {
        Intent intent=new Intent(BookCategoryActivity.this,BooksCreateActivity.class);
        startActivity(intent);
    }

    private void loadData() {
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    progressBar.dismiss();
                    yearsList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                        try {
                            BookModel book = snapshot1.getValue(BookModel.class);
                            TimeDateModel time = book.getTime();
                            if (!checkOvverride(time)) {
                                yearsList.add(time);
                            }
                        }catch (Exception e){
                            Toast.makeText(BookCategoryActivity.this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    setSpinnerData();
                }else{
                    setSpinnerData();
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setSpinnerData(){
        ArrayList<String> uniqueYears=new ArrayList<>();


        for(int i=1950; i<=2050; i++){
            String year=String.valueOf(i);
            uniqueYears.add(year);
        }

/*
        for(int j=0; j<uniqueYears.size();  j++){
            String cYear=uniqueYears.get(j).toString();

            for(int i=0; i<yearsList.size(); i++){
                String dYears= String.valueOf(yearsList.get(i).getYear());
                if(dYears.equals(cYear)){
                    uniqueYears.add(cYear);
                }
            }
        }*/









        spinnerAdapter=new ArrayAdapter(BookCategoryActivity.this,R.layout.item_layout,R.id.spinnerHeaderTExt,uniqueYears);
        yearSelectSpinner.setAdapter(spinnerAdapter);
        yearSelectSpinner.setSelection(71);

    }
    public boolean checkOvverride(TimeDateModel time){
        boolean isNone=false;
        for(int i=0; i<yearsList.size(); i++){
            TimeDateModel t=yearsList.get(i);
            if(t.getYear()==time.getYear()){
                isNone=true;
                break;
            }
        }
        return isNone;
    }


}