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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Model.ChatData;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Agent.Adapter.ChatAdapter;
import com.sohel.bookagentadmin.Agent.Model.MessageModel;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatingActivity extends AppCompatActivity {
    private ImageButton sendMessageButton,sendImageButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private Toolbar mToolbar;
    private TextView userReciverNameText;
    private CircleImageView userReciverProfileImage;


    private DatabaseReference rootRef,messageRef;
    private List<ChatData> dataList=new ArrayList<>();
    private ChatAdapter adapter;
    private ProgressDialog progressDialog;

    private DatabaseReference requestRef,chatRef;

    String senderRoom,receiverRoom,senderUid,receiverUid,recieverName,recieverImage;

    private UserShared userShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        userShared=new UserShared(this);

        requestRef=FirebaseDatabase.getInstance().getReference().child("KeyRequest");
        chatRef=FirebaseDatabase.getInstance().getReference();

        receiverUid = getIntent().getStringExtra("uid");
        recieverName = getIntent().getStringExtra("name");
        recieverImage = getIntent().getStringExtra("image");
        senderUid = userShared.getUID();

        senderRoom=senderUid+receiverUid;
        receiverRoom=receiverUid+senderUid;

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        initialize();

        adapter=new ChatAdapter(ChatingActivity.this,dataList);
        userMessagesList.setAdapter(adapter);



        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=userMessageInput.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatingActivity.this, "Please Write Something", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage2(message);
                }

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        chatRef.child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dataList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                ChatData message = snapshot1.getValue(ChatData.class);
                                dataList.add(message);
                                progressDialog.dismiss();
                                adapter.notifyDataSetChanged();
                                //   userMessagesList.scrollToPosition(userMessagesList.getAdapter().getItemCount());
                                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                            }
                        }else{ progressDialog.dismiss();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

/*
        requestRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            dataList.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ChatData message = snapshot1.getValue(ChatData.class);

                                dataList.add(message);
                                progressDialog.dismiss();
                                adapter.notifyDataSetChanged();
                                //   userMessagesList.scrollToPosition(userMessagesList.getAdapter().getItemCount());
                                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                            }
                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
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

        userReciverNameText.setText(recieverName);
        Picasso.get().load(recieverImage)
                .placeholder(R.drawable.profile)
                .into(userReciverProfileImage);

        sendMessageButton=findViewById(R.id.send_message_btn);
        userMessageInput=findViewById(R.id.input_message);
        sendImageButton=findViewById(R.id.send_files_btn);
        userMessagesList=findViewById(R.id.private_message_list_of_users);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(new LinearLayoutManager(this));




    }

    private void sendMessage2(String messageText){

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());


        String messageKey=requestRef.push().getKey()+System.currentTimeMillis();


        HashMap<String,Object> messageTextBody=new HashMap();
        messageTextBody.put("message",messageText);
        messageTextBody.put("from",userShared.getUID());
        messageTextBody.put("to",receiverUid);
        messageTextBody.put("type","text");
        messageTextBody.put("messageId",messageKey);
        messageTextBody.put("time",currenttime);
        messageTextBody.put("date",currentdate);



        HashMap<String,Object> lastMessageObject=new HashMap<>();
        lastMessageObject.put("lastMessage",messageText);
        lastMessageObject.put("date",currentdate);
        lastMessageObject.put("time",currenttime);

        chatRef.child("chats").child(senderRoom).updateChildren(lastMessageObject);
        chatRef.child("chats").child(receiverRoom).updateChildren(lastMessageObject);


        chatRef.child("chats")
                .child(senderRoom)
                .child("messages")
                .child(messageKey)
                .updateChildren(messageTextBody)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        chatRef.child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(messageKey)
                                .setValue(messageTextBody)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userMessageInput.setText("");
                                    }
                                });



                    }
                });
    }

}