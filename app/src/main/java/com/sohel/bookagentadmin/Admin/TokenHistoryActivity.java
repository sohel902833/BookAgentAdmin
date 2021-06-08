package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.TokenAdapter;
import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.Agent.AgentMainActivity;
import com.sohel.bookagentadmin.Agent.Model.AccessToken;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class TokenHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView tokenRecyclerView;
    private TokenAdapter tokenAdapter;
    private List<AccessToken> tokenList=new ArrayList<>();


    private DatabaseReference tokenRef;
    private ProgressDialog progressDialog;


    String userType,agentId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_history);

        userType=getIntent().getStringExtra("userType");

        if(userType.equals("agent")){
            agentId=getIntent().getStringExtra("agentId");
        }



        progressDialog=new ProgressDialog(this);
        tokenRef= FirebaseDatabase.getInstance().getReference().child("Tokens");

        toolbar=findViewById(R.id.tokenAppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Token List");

        tokenRecyclerView=findViewById(R.id.tokenkRecyclerViewid);
        tokenRecyclerView.setHasFixedSize(true);
        tokenRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tokenAdapter=new TokenAdapter(this,tokenList);
        tokenRecyclerView.setAdapter(tokenAdapter);

        tokenAdapter.setOnItemClickListner(new TokenAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(TokenHistoryActivity.this, "Click", Toast.LENGTH_SHORT).show();
                AccessToken token=tokenList.get(position);
                copyToClipBoard(token.getToken());
            }

            @Override
            public void onAgentViewClick(int position) {
                AccessToken token=tokenList.get(position);
                Intent intent=new Intent(TokenHistoryActivity.this,AdminUserProfileActivity.class);
                intent.putExtra("userType","Agent");
                intent.putExtra("userId",token.getAgentId());
                startActivity(intent);
            }

            @Override
            public void onUserViewClick(int position) {
                AccessToken token=tokenList.get(position);
                Intent intent=new Intent(TokenHistoryActivity.this,AdminUserProfileActivity.class);
                intent.putExtra("userType","User");
                intent.putExtra("userId",token.getUserId());
                startActivity(intent);
            }

            @Override
            public void onCopyToken(int position) {
                AccessToken token=tokenList.get(position);
                copyToClipBoard(token.getToken());
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading");
        progressDialog.show();

        if(userType.equals("agent")){
            loadAgentToken();
        }else{
            loadAllToken();
        }
    }
    private void copyToClipBoard(String text) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(
                "uid", // What should I set for this "label"?
                text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(TokenHistoryActivity.this, "Token Copy to clip board", Toast.LENGTH_SHORT).show();
    }
    public void loadAgentToken(){
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tokenList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        AccessToken token=snapshot1.getValue(AccessToken.class);
                        if(token.getAgentId().equals(agentId)){
                            tokenList.add(token);
                            tokenAdapter.notifyDataSetChanged();
                        }
                    }
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadAllToken(){
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tokenList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        AccessToken token=snapshot1.getValue(AccessToken.class);
                        tokenList.add(token);
                        tokenAdapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}