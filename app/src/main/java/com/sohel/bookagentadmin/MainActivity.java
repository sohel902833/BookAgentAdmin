package com.sohel.bookagentadmin;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.AgentListActivity;
import com.sohel.bookagentadmin.Admin.BalanceStatementActivity;
import com.sohel.bookagentadmin.Admin.BookCategoryActivity;
import com.sohel.bookagentadmin.Admin.Model.BalanceHistory;
import com.sohel.bookagentadmin.Admin.SendBalanceActivity;
import com.sohel.bookagentadmin.Admin.SendMoneyDetailsActivity;
import com.sohel.bookagentadmin.Admin.TokenHistoryActivity;
import com.sohel.bookagentadmin.Admin.TotalSellActivity;
import com.sohel.bookagentadmin.Agent.AgentMainActivity;
import com.sohel.bookagentadmin.Agent.UserListActivity;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView booksCard,agentsCard,logoutCard,addBalanceCard;
    private CardView addBalanceHistory,totalSellCard,sendBalanceCard,sendBalanceStatements,tokenCard;
    UserShared userShared;
    private Toolbar toolbar;

    private TextView adminBalanceTv;
    private DatabaseReference adminRef;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userShared=new UserShared(this);
        progressDialog=new ProgressDialog(this);

        adminRef=FirebaseDatabase.getInstance().getReference().child("Admin");

        toolbar=findViewById(R.id.mainAppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Home");


        booksCard=findViewById(R.id.booksCard);
        agentsCard=findViewById(R.id.agentsCard);
        logoutCard=findViewById(R.id.logoutCard);
        addBalanceCard=findViewById(R.id.addBalanceCArd);
        addBalanceHistory=findViewById(R.id.balanceHistoryCard);
        sendBalanceCard=findViewById(R.id.sendBalanceCard);
        sendBalanceStatements=findViewById(R.id.sendbalanceHistoryCard);
        adminBalanceTv=findViewById(R.id.adminBalanceTextViewiid);
        tokenCard=findViewById(R.id.tokenCard);
        totalSellCard=findViewById(R.id.totalSellCard);



        booksCard.setOnClickListener(this);
        agentsCard.setOnClickListener(this);
        logoutCard.setOnClickListener(this);
        addBalanceCard.setOnClickListener(this);
        addBalanceHistory.setOnClickListener(this);
        sendBalanceCard.setOnClickListener(this);
        sendBalanceStatements.setOnClickListener(this);
        tokenCard.setOnClickListener(this);
        totalSellCard.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading..");
        progressDialog.show();

        adminRef.child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("accountBalance")){
                                int balance=snapshot.child("accountBalance").getValue(Integer.class);
                                adminBalanceTv.setText("Balance:"+balance);
                                progressDialog.dismiss();

                            }else{
                                progressDialog.dismiss();
                                adminBalanceTv.setText("Balance:0");
                            }
                        }else{
                            progressDialog.dismiss();
                            adminBalanceTv.setText("Balance:0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.booksCard){
            startActivity(new Intent(MainActivity.this, BookCategoryActivity.class));
        }else if(v.getId()==R.id.agentsCard){
            startActivity(new Intent(MainActivity.this, AgentListActivity.class));
        }else if(v.getId()==R.id.logoutCard){
            logout();
        }else if(v.getId()==R.id.addBalanceCArd){
            showAddBalanceDialouge();
        }else if(v.getId()==R.id.balanceHistoryCard){
            startActivity(new Intent(MainActivity.this, BalanceStatementActivity.class));
        }else if(v.getId()==R.id.sendBalanceCard){
            startActivity(new Intent(MainActivity.this, SendBalanceActivity.class));
        }else if(v.getId()==R.id.sendbalanceHistoryCard){
            startActivity(new Intent(MainActivity.this, SendMoneyDetailsActivity.class));
        }else if(v.getId()==R.id.tokenCard){
            sendUserToTokenDetailsActivity();
        }else if(v.getId()==R.id.totalSellCard){
            startActivity(new Intent(MainActivity.this, TotalSellActivity.class));
        }
    }

    private void sendUserToTokenDetailsActivity() {
        Intent intent=new Intent(MainActivity.this, TokenHistoryActivity.class);
        intent.putExtra("userType","admin");
        startActivity(intent);
    }

    private void logout() {
        UserShared userShared=new UserShared(MainActivity.this);
        userShared.logoutUser();
        sendUserToLoginActivity();
        
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAddBalanceDialouge() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.add_balance_diolouge,null);
        builder.setView(view);

        EditText balanceEt=view.findViewById(R.id.balance_EditText);
        Button addButton=view.findViewById(R.id.addBalaceId);


        final AlertDialog dialog=builder.create();
        dialog.show();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String balance=balanceEt.getText().toString();
                if(balance.isEmpty()){
                    balanceEt.setError("Please Enter Balance");
                    balanceEt.requestFocus();
                }else{
                    checkBalanceToDatabase(Integer.parseInt(balance),dialog);
                }


            }
        });
    }

    private void checkBalanceToDatabase(int balance,AlertDialog dialog) {
    progressDialog.setMessage("Adding Balance");
    progressDialog.setTitle("Please Wait.");
    progressDialog.show();
        adminRef.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int prevBalance=snapshot.child("accountBalance").getValue(Integer.class);
                    setBalanceIntoDatabase(balance,balance+prevBalance,dialog);
                }else{
                    setBalanceIntoDatabase(balance,balance,dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }

    private void setBalanceIntoDatabase(int prevBalance,int balance, AlertDialog dialog) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());

        String id= String.valueOf(System.currentTimeMillis());

        BalanceHistory balanceHistory=new BalanceHistory(id,currentdate,currenttime,prevBalance);

        adminRef.child("BalanceHistory")
                .child(id)
                .setValue(balanceHistory)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        adminRef.child("Profile")
                                 .child("accountBalance")
                                .setValue(balance).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    onStart();
                                    Toast.makeText(MainActivity.this, "Balance Added Successful", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "Error:", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error:", Toast.LENGTH_SHORT).show();
                    }
                });




    }


}