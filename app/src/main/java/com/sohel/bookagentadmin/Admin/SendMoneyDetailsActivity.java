package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.SendMoneyAdapter;
import com.sohel.bookagentadmin.Admin.Model.BalanceHistory;
import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SendMoneyDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<SendBalanceModel> sendBalanceModelList=new ArrayList<>();

    private DatabaseReference sendMoneyRef;
    private SendMoneyAdapter sendMoneyAdapter;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_details);

        toolbar=findViewById(R.id.sendMoneyDetailsAppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Send Balance Statements");

        progressDialog=new ProgressDialog(this);
        sendMoneyRef= FirebaseDatabase.getInstance().getReference().child("SendMoneyHistory");



        recyclerView=findViewById(R.id.sendMoneyDetailsRecyclerViewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sendMoneyAdapter=new SendMoneyAdapter(this,sendBalanceModelList);

        recyclerView.setAdapter(sendMoneyAdapter);


        sendMoneyAdapter.setOnItemClickListner(new SendMoneyAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDelete(int position) {
                SendBalanceModel model=sendBalanceModelList.get(position);
                deleteStatement(model.getId());
            }
        });


    }


    private void deleteStatement(String id) {

        progressDialog.setMessage("Deleting..");
        sendMoneyRef.child(id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(SendMoneyDetailsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SendMoneyDetailsActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading");
        progressDialog.show();

        sendMoneyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sendBalanceModelList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        SendBalanceModel balance=snapshot1.getValue(SendBalanceModel.class);
                        sendBalanceModelList.add(balance);
                        sendMoneyAdapter.notifyDataSetChanged();
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