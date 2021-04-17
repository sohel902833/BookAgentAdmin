package com.sohel.bookagentadmin.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sohel.bookagentadmin.R;

public class BooksCreateActivity extends AppCompatActivity {

    ImageView imageView;
    EditText book_names_editText;
    Button saveBooks,choseImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_create);

        imageView=findViewById(R.id.image_id);
        book_names_editText=findViewById(R.id.editText_id);

        saveBooks=findViewById(R.id.save_books_btn_id);
        choseImage=findViewById(R.id.chose_img_btn_id);





    }
}