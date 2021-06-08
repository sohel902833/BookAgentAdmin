package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminUserProfileActivity extends AppCompatActivity {

    private TextView coinTv,emailTv,nameTv,phoneTv,uidTv,uidHeaderTv;
    private CircleImageView profileImage;

    private Toolbar toolbar;

    private String userType="",userId="";

    private DatabaseReference agentRef,userRef,coinRef;
    private ProgressDialog progressDialog;

    private Users user;
    private Agent agent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_profile);

        userType=getIntent().getStringExtra("userType");
        userId=getIntent().getStringExtra("userId");

        toolbar=findViewById(R.id.u_a_AppBarid);
        setSupportActionBar(toolbar);

        progressDialog=new ProgressDialog(this);
        agentRef= FirebaseDatabase.getInstance().getReference("Admin").child("AgentList");
        userRef= FirebaseDatabase.getInstance().getReference("users");
        coinRef= FirebaseDatabase.getInstance().getReference("AgentCoins");

        coinTv=findViewById(R.id.u_a_CoinTv);
        emailTv=findViewById(R.id.u_a_emailTextViewid);
        nameTv=findViewById(R.id.u_a_UserNameTv);
        phoneTv=findViewById(R.id.u_a_userPhoneTv);
        uidTv=findViewById(R.id.u_a_UIDTextViewid);
        uidHeaderTv=findViewById(R.id.u_a_CopyHeaderTv);
        profileImage=findViewById(R.id.u_a_ProfileImageViewid);

        uidHeaderTv.setText("Tap To Copy "+userType+" UID");

        if(userType.equals("User")){
            coinTv.setVisibility(View.GONE);
        }
        if(userType.equals("Agent")){
            emailTv.setVisibility(View.GONE);
        }



        uidTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equals("Agent")){
                    if(agent!=null){
                        copyToClipBoard(agent.getuID());
                    }
                }else if(userType.equals("User")){
                    if(user!=null){
                        copyToClipBoard(user.getUid());
                    }
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading..");
        progressDialog.show();

        if(userType.equals("Agent")){
            loadAgentData();
        }else if(userType.equals("User")){
            loadUserData();
        }



    }
    private void copyToClipBoard(String text) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(
                "uid", // What should I set for this "label"?
                text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(AdminUserProfileActivity.this, "Uid Copy to clip board", Toast.LENGTH_SHORT).show();
    }

    private void loadUserData() {
        userRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                             user=snapshot.getValue(Users.class);
                            String userImage=snapshot.child("image").getValue().toString();

                           Picasso.get().load(userImage).placeholder(R.drawable.profile)
                                    .into(profileImage);

                            phoneTv.setText(user.getPhone());
                            nameTv.setText(user.getName());
                            uidTv.setText(user.getUid());
                            emailTv.setText(user.getEmail());


                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAgentData() {

        agentRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            agent=snapshot.getValue(Agent.class);
                            String agentImage=snapshot.child("profileImage").getValue().toString();

                            getAgentCoin(agent.getuID(),agentImage);
                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void getAgentCoin(String agentId,String agentImage) {

        coinRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int coin=snapshot.child("coins").getValue(Integer.class);
                           Picasso.get().load(agentImage).placeholder(R.drawable.profile)
                                    .into(profileImage);

                            phoneTv.setText(agent.getPhone());
                            nameTv.setText(agent.getName());
                            uidTv.setText(agent.getuID());
                            coinTv.setText(""+coin);
                            progressDialog.dismiss();
                        }else{
                          Picasso.get().load(agentImage).placeholder(R.drawable.profile)
                                    .into(profileImage);
                            phoneTv.setText(agent.getPhone());
                            nameTv.setText(agent.getName());
                            uidTv.setText(agent.getuID());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}