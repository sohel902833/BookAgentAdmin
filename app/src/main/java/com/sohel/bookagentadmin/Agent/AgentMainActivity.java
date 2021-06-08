package com.sohel.bookagentadmin.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.RequestUserAdapter;
import com.sohel.bookagentadmin.Admin.AdminUserProfileActivity;
import com.sohel.bookagentadmin.Admin.GenerateTokenActivity;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Admin.TokenHistoryActivity;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;
import com.sohel.bookagentadmin.LoginActivity;
import com.sohel.bookagentadmin.MainActivity;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgentMainActivity extends AppCompatActivity {

    private DatabaseReference requestRef,userRef,agentREf,coinRef;
    private  ProgressDialog progressBar;
    private TextView cointTv,nameTv,emailTv;
    CircleImageView circleImageView;

    private CardView msgCard,logoutCard,tokenCard,generateTokenCard,sendMoneyCard,profileCard;
    private  UserShared userShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_main);

        userShared=new UserShared(this);


        requestRef= FirebaseDatabase.getInstance().getReference().child("KeyRequest");
        userRef= FirebaseDatabase.getInstance().getReference("users");
        agentREf= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        coinRef= FirebaseDatabase.getInstance().getReference("AgentCoins");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();



        msgCard=findViewById(R.id.tokenRequestCard);
        logoutCard=findViewById(R.id.logoutCard);
        tokenCard=findViewById(R.id.tokenCard);
        generateTokenCard=findViewById(R.id.generateTokenCard);
        sendMoneyCard=findViewById(R.id.sendMoneyCard);
        profileCard=findViewById(R.id.profileCard);

        circleImageView=findViewById(R.id.profileImage);
        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);
        cointTv=findViewById(R.id.coinTv);

        loadUserInfo();
        msgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentMainActivity.this,UserListActivity.class));
            }
        });
        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        tokenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AgentMainActivity.this, TokenHistoryActivity.class);
                intent.putExtra("userType","agent");
                intent.putExtra("agentId",userShared.getUID());
                startActivity(intent);
            }
        });
        generateTokenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentMainActivity.this, GenerateTokenActivity.class));
            }
        });


        sendMoneyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AgentMainActivity.this,AgentSendMoneyActivity.class);
                startActivity(intent);
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AgentMainActivity.this, AdminUserProfileActivity.class);
                intent.putExtra("userType","Agent");
                intent.putExtra("userId",userShared.getUID());
                startActivity(intent);
            }
        });


    }

    private void loadUserInfo() {
        agentREf.child(userShared.getUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Agent ag=snapshot.getValue(Agent.class);
                            getAgentCoin(ag);
                        }else{
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void getAgentCoin(Agent ag) {

        coinRef.child(ag.getuID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("coins")){
                                int coin=snapshot.child("coins").getValue(Integer.class);
                                nameTv.setText(ag.getName());
                                cointTv.setText(""+coin);
                                emailTv.setText(ag.getPhone());
                                Picasso.get().load(ag.getProfileImage()).placeholder(R.drawable.profile).into(circleImageView);

                                progressBar.dismiss();


                            }
                            }else{
                            nameTv.setText(ag.getName());
                            cointTv.setText("0");
                            emailTv.setText(ag.getPhone());
                            Picasso.get().load(ag.getProfileImage()).placeholder(R.drawable.profile).into(circleImageView);
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void logout() {
        UserShared userShared=new UserShared(AgentMainActivity.this);
        userShared.logoutUser();
        sendUserToLoginActivity();
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(AgentMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}