package com.sohel.bookagentadmin.Admin;

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
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SendBalanceActivity extends AppCompatActivity {

    private EditText agentIdEditText,balanceEdittext;
    private Button searchButton,sendMoneyButton;
    private TextView agentNameTv,agentPhoneTv;
    private Toolbar toolbar;

    private DatabaseReference adminRef,agentRef,rootRef,coinRef;
    private ProgressDialog progressDialog;


    Agent agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_balance);
        init();

        progressDialog=new ProgressDialog(this);
        adminRef= FirebaseDatabase.getInstance().getReference().child("Admin");
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        coinRef= FirebaseDatabase.getInstance().getReference("AgentCoins");

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
                    checkAdminBalance(Integer.parseInt(balance));
                }
            }
        });




    }

    private void init() {

        toolbar=findViewById(R.id.sendBalanceAppBarid);

        setSupportActionBar(toolbar);
        this.setTitle("Send Balance");


        agentIdEditText=findViewById(R.id.agentIdEditText);
        balanceEdittext=findViewById(R.id.balanceAmountEditext);
        agentNameTv=findViewById(R.id.agentnameTextViewid);
        agentPhoneTv=findViewById(R.id.agentPhoneTextViewid);
        searchButton=findViewById(R.id.agentSearchButton);
        sendMoneyButton=findViewById(R.id.sendBalanceButtonid);
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
                             agent=snapshot.getValue(Agent.class);

                             agentNameTv.setVisibility(View.VISIBLE);
                             agentPhoneTv.setVisibility(View.VISIBLE);
                             sendMoneyButton.setVisibility(View.VISIBLE);
                             balanceEdittext.setVisibility(View.VISIBLE);

                             agentNameTv.setText("Agent Name: "+agent.getName());
                             agentPhoneTv.setText("Agent Phone: "+agent.getPhone());
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(SendBalanceActivity.this, "Agent Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void checkAdminBalance(int entierBalance) {
        progressDialog.setMessage("Sending...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        sendMoneyButton.setEnabled(false);

        adminRef.child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("accountBalance")){
                                int adminBalance=snapshot.child("accountBalance").getValue(Integer.class);
                                if(adminBalance<entierBalance){
                                    progressDialog.dismiss();
                                    sendMoneyButton.setEnabled(true);
                                    Toast.makeText(SendBalanceActivity.this, "You don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                                }else{
                                    sendMoneyButton.setEnabled(false);
                                    getAgentCoin(agent.getuID(),entierBalance,adminBalance);
                                }
                            }
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(SendBalanceActivity.this, "You Don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAgentCoin(String agentId,int enterBalance,int adminBalance) {

        coinRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int coin=snapshot.child("coins").getValue(Integer.class);
                             sendBalanceToAgent(coin,enterBalance,adminBalance);
                        }else{
                          sendBalanceToAgent(0,enterBalance,adminBalance);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void sendBalanceToAgent(int coin,int enterBalance,int adminBalance) {

        String id= String.valueOf(System.currentTimeMillis());
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());

        SendBalanceModel model=new SendBalanceModel(
                id,
                "Admin",
                "Admin",
                agent.getuID(),
                agent.getName(),
                currenttime,
                currentdate,
                enterBalance,
                coin+enterBalance,
                adminBalance-enterBalance
        );

        rootRef.child("SendMoneyHistory")
                .child(id)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            removeAdminBAlance(enterBalance,adminBalance,coin);
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(SendBalanceActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void removeAdminBAlance(int enterBalance,int adminBalance,int agentCoin) {

        adminRef.child("Profile")
                .child("accountBalance")
                .setValue(adminBalance-enterBalance)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            addBalanceToAgent(enterBalance,agentCoin);
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(SendBalanceActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addBalanceToAgent(int enterBalance,int agentCoin) {
        coinRef.child(agent.getuID())
                .child("coins")
                .setValue(agentCoin+enterBalance)
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

                            Toast.makeText(SendBalanceActivity.this, "Send Balance Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            sendMoneyButton.setEnabled(true);
                            progressDialog.dismiss();
                            Toast.makeText(SendBalanceActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}