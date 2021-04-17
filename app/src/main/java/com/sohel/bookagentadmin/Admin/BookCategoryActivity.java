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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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



    //Firebase
    private DatabaseReference categoryRef;
    private StorageReference storageReference;

    private List<BookCategory> bookCategoryList=new ArrayList<>();
    private BookCategoryAdapter categoryAdapter;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_category);

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

        addBookCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategooryAddDiolouge();
            }
        });


        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        categoryAdapter=new BookCategoryAdapter(this,bookCategoryList);
        recyclerView.setAdapter(categoryAdapter);


        categoryAdapter.setOnItemClickListner(new BookCategoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
               /* Category category=categoryList.get(position);
                Intent intent=new Intent(AdminUtilitiesCategory.this,UtilitiesListActivity.class);
                intent.putExtra("categoryId",category.getCategoryId());
                intent.putExtra("categoryName",category.getCategoryName());
                startActivity(intent);
*/
            }

            @Override
            public void onDelete(int position) {

            }

            @Override
            public void onUpdate(int position) {
                BookCategory currentCategor=bookCategoryList.get(position);
                showCategoryUpdateDialog(currentCategor);
            }
        });










    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    bookCategoryList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        BookCategory category=snapshot1.getValue(BookCategory.class);
                        bookCategoryList.add(category);
                        categoryAdapter.notifyDataSetChanged();
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
    public void deleteCategory(String uid){
        progressBar.setMessage("Deleting Category.");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        categoryRef.child(uid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressBar.dismiss();
                            Toasty.success(BookCategoryActivity.this, "Category Deleted", Toast.LENGTH_SHORT, true).show();
                        }else{
                            progressBar.dismiss();
                            Toasty.warning(BookCategoryActivity.this, "Category Delete Failed,Please Try again Later.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }
    private void showCategoryUpdateDialog(BookCategory category) {
        AlertDialog.Builder builder=new AlertDialog.Builder(BookCategoryActivity.this);
        View view=getLayoutInflater().inflate(R.layout.category_add_diolouge,null);
        builder.setView(view);

        categoryImageView=view.findViewById(R.id.utiliti_diolouge_ImageViewid);
        EditText editText=view.findViewById(R.id.utiliti_diolouge_CategoryNameEdittextid);
        Button saveButton=view.findViewById(R.id.categorySaveButtonid);

        Picasso.get().load(category.getImage()).into(categoryImageView);
        editText.setText(category.getCategoryName());
        saveButton.setText("Update Category");
        final AlertDialog dialog=builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName=editText.getText().toString();

                if(categoryName.isEmpty()){
                    editText.setError("Please Write Category Name");
                    editText.requestFocus();
                }else{
                    updateCategory(category,categoryName,dialog);
                }
            }
        });
        categoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfilechooser();
            }
        });





    }
    private void showCategooryAddDiolouge() {
        AlertDialog.Builder builder=new AlertDialog.Builder(BookCategoryActivity.this);
        View view=getLayoutInflater().inflate(R.layout.category_add_diolouge,null);
        builder.setView(view);

        categoryImageView=view.findViewById(R.id.utiliti_diolouge_ImageViewid);
        EditText editText=view.findViewById(R.id.utiliti_diolouge_CategoryNameEdittextid);
        Button saveButton=view.findViewById(R.id.categorySaveButtonid);
        final AlertDialog dialog=builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName=editText.getText().toString();
                if(imageUri==null){
                    Toasty.warning(BookCategoryActivity.this, "Please Choose An Image First", Toast.LENGTH_SHORT, true).show();
                }
                else if(categoryName.isEmpty()){
                    editText.setError("Please Write Category Name");
                    editText.requestFocus();
                }else{
                    saveCategory(categoryName,dialog);
                }
            }
        });
        categoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfilechooser();
            }
        });





    }
    private void saveCategory(String categoryName,AlertDialog dialog) {
        progressBar.setMessage("Saving Category.");
        progressBar.setTitle("Please Wait");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        String key=categoryRef.push().getKey();

        StorageReference filePath=storageReference.child(key+categoryRef.push().getKey()+"."+getFileExtension(imageUri));
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloaduri=urlTask.getResult();

                String categoryId=key+System.currentTimeMillis()+System.currentTimeMillis();

                HashMap<String,String> categoryMap=new HashMap<>();
                categoryMap.put("categoryName",categoryName);
                categoryMap.put("categoryId",categoryId);
                categoryMap.put("image",downloaduri.toString());


                categoryRef.child(categoryId)
                        .setValue(categoryMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressBar.dismiss();
                                    dialog.dismiss();
                                    Toasty.success(BookCategoryActivity.this, "Category Saved", Toast.LENGTH_SHORT, true).show();
                                }else{
                                    progressBar.dismiss();
                                    Toasty.warning(BookCategoryActivity.this, "Category Save Failed,Please Try again Later.", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });

            }
        });





    }
    private void updateCategory(BookCategory category,String categoryName,AlertDialog dialog) {
        progressBar.setMessage("Saving Category.");
        progressBar.setTitle("Please Wait");
        progressBar.setCanceledOnTouchOutside(false);

        if(imageUri==null){
            progressBar.show();
            categoryRef.child(category.getId())
                    .child(categoryName)
                    .setValue(categoryName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.dismiss();
                                dialog.dismiss();
                                Toasty.success(BookCategoryActivity.this, "Category Update Success", Toast.LENGTH_SHORT, true).show();
                            }else{
                                progressBar.dismiss();
                                Toasty.warning(BookCategoryActivity.this, "Category Update Failed,Please Try again Later.", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });
        }else{
            progressBar.show();
            String key=categoryRef.push().getKey();

            StorageReference filePath=storageReference.child(key+categoryRef.push().getKey()+"."+getFileExtension(imageUri));
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!urlTask.isSuccessful());
                    Uri downloaduri=urlTask.getResult();
                    HashMap<String,Object> categoryMap=new HashMap<>();
                    categoryMap.put("categoryName",categoryName);
                    categoryMap.put("image",downloaduri.toString());


                    categoryRef.child(category.getId())
                            .updateChildren(categoryMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.dismiss();
                                        dialog.dismiss();
                                        Toasty.success(BookCategoryActivity.this, "Category Updated", Toast.LENGTH_SHORT, true).show();
                                    }else{
                                        progressBar.dismiss();
                                        Toasty.warning(BookCategoryActivity.this, "Category Update Failed,Please Try again Later.", Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                            });

                }
            });
        }

    }


    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }

    private void openfilechooser() {
        Intent intentf=new Intent();
        intentf.setType("image/*");
        intentf.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentf,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null){
            imageUri=data.getData();
            categoryImageView.setImageURI(data.getData());
        }

    }





}