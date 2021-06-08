package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Agent.Adapter.SpinnerCustomAdapter;
import com.sohel.bookagentadmin.Agent.Model.AccessToken;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class GenerateTokenActivity extends AppCompatActivity {

    private Button generateButton;
    private TextView tokenTextView;
    private EditText userIdEdittext;
    private Toolbar toolbar;
   Agent agent;
   Users user;
    private DatabaseReference tokenRef,userRef,agentRef,buyRef,coinRef;
    private ProgressDialog progressBar;
    private UserShared userShared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_token);

        toolbar=findViewById(R.id.generate_token_AppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Generate Token");

        progressBar=new ProgressDialog(this);

        tokenRef= FirebaseDatabase.getInstance().getReference().child("Tokens");
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        buyRef= FirebaseDatabase.getInstance().getReference().child("AgentGeneratedToken");
        coinRef= FirebaseDatabase.getInstance().getReference("AgentCoins");


        userShared=new UserShared(this);
        generateButton=findViewById(R.id.keyGenerateButton);
        tokenTextView=findViewById(R.id.generatedTextViewid);
        userIdEdittext=findViewById(R.id.userIdEdittext);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId=userIdEdittext.getText().toString().trim();
                if(userId.isEmpty()){
                    userIdEdittext.setError("Please Insert User Id");
                    userIdEdittext.requestFocus();
                    return;
                }else{
                     generateToken(userId);
                }



            }
        });




    }

    public void generateToken(String userId){

        progressBar.setMessage("Generating New Token...");
        progressBar.setTitle("Please Wait.");
        progressBar.show();
        checkUserExist(userId);

    }

    public void checkUserExist(String userId){
        userRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                             user=snapshot.getValue(Users.class);
                            String agentId=userShared.getUID();
                            checkAgentData(agentId,userId);
                        }else{
                            progressBar.dismiss();
                            Toast.makeText(GenerateTokenActivity.this, "Incorrect User Id", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.dismiss();
                    }
                });


    }

    private void checkAgentData(String agentId,String userId) {
        agentRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            agent=snapshot.getValue(Agent.class);
                            checkTimeLimit(agent,agentId,userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.dismiss();
                    }
                });
    }

    private void checkTimeLimit(Agent agent,String agentId,String userId) {
        buyRef.child(agentId)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            long time = snapshot.child("time").getValue(Long.class);
                            getAgentCoin(agent.getuID(),time,userId);

                        }else{
                            getAgentCoin(agent.getuID(),0,userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void getAgentCoin(String agentId,long time,String userId) {

        coinRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int coin=snapshot.child("coins").getValue(Integer.class);
                            if (coin < 10) {
                                progressBar.dismiss();
                                Toast.makeText(GenerateTokenActivity.this, "You Don't Have Coin for generate new Token", Toast.LENGTH_SHORT).show();
                            } else {
                                if (time > System.currentTimeMillis()) {
                                    progressBar.dismiss();
                                    Toast.makeText(GenerateTokenActivity.this, "Limit Over,You Already Geenerate Token For this user Today", Toast.LENGTH_LONG).show();
                                } else {
                                    saveToken(agentId,userId);
                                }
                            }


                        }else{
                            progressBar.dismiss();
                            Toast.makeText(GenerateTokenActivity.this, "You Don't Have Enough Coin", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void saveToken(String agentId, String userId) {
        String token=String.valueOf(System.currentTimeMillis());

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());



        AccessToken accessToken=new AccessToken(agentId,token,userId,user.getName(),agent.getName(),currentdate,currenttime,false);


        tokenRef.child(token)
                .setValue(accessToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            saveInstanceToAgentProfile(userId,agentId,token);
                       }else{
                            progressBar.dismiss();
                            Toasty.warning(GenerateTokenActivity.this, "Token Generate Failed.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    private void saveInstanceToAgentProfile(String userId, String agentId,String token) {
        long time=((60*60)*24)*1000;
        buyRef.child(agentId)
                .child(userId)
                .child("time")
                .setValue(System.currentTimeMillis()+time).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                  //  removeCoin(agentId,token);
                    if(task.isSuccessful()){
                        tokenTextView.setText(""+token);
                        progressBar.dismiss();
                        Toasty.success(GenerateTokenActivity.this, "Token Generated", Toast.LENGTH_SHORT, true).show();

                    }else{
                        Toast.makeText(GenerateTokenActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                 }else{
                    Toast.makeText(GenerateTokenActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
    public void removeCoin(String agentId,String token){
        int newCoin=agent.getCoin()-10;
        agentRef.child(agentId)
                .child("coin")
                .setValue(newCoin)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            tokenTextView.setText(""+token);
                            progressBar.dismiss();
                            Toasty.success(GenerateTokenActivity.this, "Token Generated", Toast.LENGTH_SHORT, true).show();

                        }else{
                            Toast.makeText(GenerateTokenActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
*/

  /*  public long getTimeInMillis(String txt){
        long time=0;
        if(txt.equals("5 Hours")){
            long sec=(60*60)*5;
            time=sec*1000;
        }else if(txt.equals("10 Hours")){
            long sec=(60*60)*10;
            time=sec*1000;

        }else if(txt.equals("15 Hours")){
            long sec=(60*60)*15;
            time=sec*1000;
        }else if(txt.equals("1 Day")){
            long sec=(60*60)*24;
            time=sec*1000;

        }else if(txt.equals("5 Day")){
            long sec=(60*60)*(24*5);
            time=sec*1000;

        }else if(txt.equals("10 Day")){
            long sec=(60*60)*(24*10);
            time=sec*1000;
        }else if(txt.equals("15 Day")){
            long sec=(60*60)*(24*15);
            time=sec*1000;
        }else if(txt.equals("1 Month")){
            long sec=(60*60)*(24*30);
            time=sec*1000;
        }else if(txt.equals("2 Month")){
            long sec=(60*60)*(24*60);
            time=sec*1000;
        }else if(txt.equals("3 Month")){
            long sec=(60*60)*(24*90);
            time=sec*1000;
        }else if(txt.equals("5 Month")){
            long sec=(60*60)*(24*150);
            time=sec*1000;
        }else if(txt.equals("7 Month")){
            long sec=(60*60)*(24*210);
            time=sec*1000;
        }else if(txt.equals("9 Month")){
            long sec=(60*60)*(24*270);
            time=sec*1000;
        }else if(txt.equals("10 Month")){
            long sec=(60*60)*(24*300);
            time=sec*1000;
        }else if(txt.equals("1 Years")){
            long sec=(60*60)*(24*365);
            time=sec*1000;
        }else if(txt.equals("2 Years")){
            long sec=(60*60)*(24*(365*2));
            time=sec*1000;
        }


        return time;
    }
*/



}