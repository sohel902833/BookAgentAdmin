package com.sohel.bookagentadmin;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.AgentListActivity;
import com.sohel.bookagentadmin.Admin.BookCategoryActivity;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CardView booksCard,agentsCard,usercard,logoutCard;

    private CircleImageView profileImage;
    private  TextView nameTv,emailTv;

        UserShared userShared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userShared=new UserShared(this);

        booksCard=findViewById(R.id.booksCard);
        agentsCard=findViewById(R.id.agentsCard);
        usercard=findViewById(R.id.userCards);
        logoutCard=findViewById(R.id.logoutCard);
        profileImage=findViewById(R.id.profileImage);
        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);


        emailTv.setText(userShared.getUserPhone());




        booksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BookCategoryActivity.class));
            }
        });

        agentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AgentListActivity.class));
            }
        });



    }


}