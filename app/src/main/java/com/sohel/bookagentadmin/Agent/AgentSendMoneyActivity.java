package com.sohel.bookagentadmin.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.Admin.SendBalanceActivity;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AgentSendMoneyActivity extends AppCompatActivity {
    private EditText agentIdEditText,balanceEdittext;
    private Button searchButton,sendMoneyButton;
    private TextView agentNameTv,agentPhoneTv;
    private Toolbar toolbar;

    private DatabaseReference adminRef,agentRef,rootRef,coinRef;
    private ProgressDialog progressDialog;

    Agent submitAgent,currentAgent;
    UserShared userShared;
    int currentAgentCoin=0,submitedAgentCoin=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_send_money);


        userShared=new UserShared(this);
        init();

        progressDialog=new ProgressDialog(this);
        adminRef= FirebaseDatabase.getInstance().getReference().child("Admin");
        coinRef= FirebaseDatabase.getInstance().getReference().child("AgentCoins");
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        rootRef=FirebaseDatabase.getInstance().getReference();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agentId=agentIdEditText.getText().toString().trim();
                if(agentId.isEmpty()){
                    agentIdEditText.setError("Please Insert An Agent Id");
                    agentIdEditText.requestFocus();
                }else{
                    findAgentDetails(agentId);
                }
            }
        });


        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String balance=balanceEdittext.getText().toString().trim();

                if(balance.isEmpty()){
                    balanceEdittext.setError("Enter Some Balance");
                    balanceEdittext.requestFocus();
                }else{
                    checkAgentBalance(Integer.parseInt(balance));
                }
            }
        });




    }


    private void init() {

        toolbar=findViewById(R.id.ag_sendBalanceAppBarid);

        setSupportActionBar(toolbar);
        this.setTitle("Send Balance");
        agentIdEditText=findViewById(R.id.ag_agentIdEditText);
        balanceEdittext=findViewById(R.id.ag_balanceAmountEditext);
        agentNameTv=findViewById(R.id.ag_agentnameTextViewid);
        agentPhoneTv=findViewById(R.id.ag_agentPhoneTextViewid);
        searchButton=findViewById(R.id.ag_agentSearchButton);
        sendMoneyButton=findViewById(R.id.ag_sendBalanceButtonid);
    }

    private void findAgentDetails(String agentId) {

        progressDialog.setMessage("Finding Agent");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        agentRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                            submitAgent=snapshot.getValue(Agent.class);

                            agentNameTv.setVisibility(View.VISIBLE);
                            agentPhoneTv.setVisibility(View.VISIBLE);
                            sendMoneyButton.setVisibility(View.VISIBLE);
                            balanceEdittext.setVisibility(View.VISIBLE);

                            agentNameTv.setText("Agent Name: "+submitAgent.getName());
                            agentPhoneTv.setText("Agent Phone: "+submitAgent.getPhone());
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(AgentSendMoneyActivity.this, "Agent Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkAgentBalance(int entierBalance) {
        progressDialog.setMessage("Sending...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        sendMoneyButton.setEnabled(false);

        agentRef.child(userShared.getUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                             currentAgent=snapshot.getValue(Agent.class);
                             getCurrentAgentCoin(currentAgent,entierBalance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void getCurrentAgentCoin(Agent currentAgent,int entierBalance) {

        coinRef.child(currentAgent.getuID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            currentAgentCoin=snapshot.child("coins").getValue(Integer.class);

                            if(currentAgentCoin<entierBalance){
                                progressDialog.dismiss();
                                Toast.makeText(AgentSendMoneyActivity.this, "You don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                            }else {
                                sendMoneyButton.setEnabled(false);
                               // sendBalanceToAgent(entierBalance);
                                getSubmitedAgentCoin(entierBalance);
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(AgentSendMoneyActivity.this, "You don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getSubmitedAgentCoin(int entierBalance){

        coinRef.child(submitAgent.getuID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            submitedAgentCoin=snapshot.child("coins").getValue(Integer.class);
                            sendBalanceToAgent(entierBalance);
                        }else{
                            sendBalanceToAgent(entierBalance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void sendBalanceToAgent(int enterBalance) {

        String id= String.valueOf(System.currentTimeMillis());
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());

        SendBalanceModel model=new SendBalanceModel(
                id,
                currentAgent.getuID(),
                currentAgent.getName(),
                submitAgent.getuID(),
                submitAgent.getName(),
                currenttime,
                currentdate,
                enterBalance,
                submitedAgentCoin+enterBalance,
                currentAgentCoin-enterBalance
        );

        rootRef.child("SendMoneyHistory")
                .child(id)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            removeCurrentAgentBalance(enterBalance);
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(AgentSendMoneyActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void removeCurrentAgentBalance(int enterBalance) {

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("coins",currentAgentCoin-enterBalance);

        coinRef.child(currentAgent.getuID())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            addBalanceToAgent(enterBalance);
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(AgentSendMoneyActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
    private void addBalanceToAgent(int enterBalance) {

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("coins",submitedAgentCoin+enterBalance);


        coinRef.child(submitAgent.getuID())
               .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();

                            sendMoneyButton.setEnabled(true);
                            balanceEdittext.setText("");
                            agentNameTv.setVisibility(View.GONE);
                            agentPhoneTv.setVisibility(View.GONE);
                            sendMoneyButton.setVisibility(View.GONE);
                            balanceEdittext.setVisibility(View.GONE);

                            Toast.makeText(AgentSendMoneyActivity.this, "Send Balance Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(AgentSendMoneyActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}