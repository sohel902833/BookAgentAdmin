package com.sohel.bookagentadmin.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Agent.Adapter.ChatAdapter;
import com.sohel.bookagentadmin.Agent.Model.MessageModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatingActivity extends AppCompatActivity {
    String reciverUserid,fullName,profileImage,senderUserid;
    private ImageButton sendMessageButton,sendImageButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private Toolbar mToolbar;
    private TextView userReciverNameText;
    private CircleImageView userReciverProfileImage;




    private DatabaseReference rootRef,messageRef;
    private FirebaseAuth mAuth;
    private List<MessageModel> dataList=new ArrayList<>();
    private ChatAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);



        reciverUserid=getIntent().getStringExtra("uid");
        fullName=getIntent().getStringExtra("name");

        rootRef= FirebaseDatabase.getInstance().getReference().child("fc");
        messageRef= FirebaseDatabase.getInstance().getReference().child("fc").child("Message");
        messageRef.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();
        senderUserid=mAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        initialize();

        displayReciverInfo();













    }

    public void initialize(){

        mToolbar=findViewById(R.id.chat_Toolbarid);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(view);

        userReciverNameText=view.findViewById(R.id.customProfileName);
        userReciverProfileImage=view.findViewById(R.id.custom_profile_Image);


        sendMessageButton=findViewById(R.id.send_message_btn);
        userMessageInput=findViewById(R.id.input_message);
        sendImageButton=findViewById(R.id.send_files_btn);
        userMessagesList=findViewById(R.id.private_message_list_of_users);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(new LinearLayoutManager(this));


    }
    private void displayReciverInfo() {

        rootRef.child("Users").child(reciverUserid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            if (snapshot.hasChild("profileImage")) {
                                profileImage = snapshot.child("profileImage").getValue().toString();
                            } else {
                                profileImage = "none";
                            }

                            userReciverNameText.setText(fullName);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(userReciverProfileImage);



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}