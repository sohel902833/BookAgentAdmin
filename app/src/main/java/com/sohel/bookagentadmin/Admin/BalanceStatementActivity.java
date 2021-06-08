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
import com.sohel.bookagentadmin.Admin.Adapter.BalanceStatementAdapter;
import com.sohel.bookagentadmin.Admin.Model.BalanceHistory;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class BalanceStatementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private List<BalanceHistory> balanceHistoryList=new ArrayList<>();
    private DatabaseReference adminRef;
    private BalanceStatementAdapter balanceAdapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_statement);

        progressDialog=new ProgressDialog(this);
        toolbar=findViewById(R.id.balanceStatementToolbarid);
        setSupportActionBar(toolbar);
        this.setTitle("Balance Statements");

        adminRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("BalanceHistory");


        recyclerView=findViewById(R.id.balanceStatementRecyclerViewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        balanceAdapter=new BalanceStatementAdapter(this,balanceHistoryList);

        recyclerView.setAdapter(balanceAdapter);

        balanceAdapter.setOnItemClickListner(new BalanceStatementAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDelete(int position) {
                BalanceHistory balance=balanceHistoryList.get(position);
                deleteStatement(balance.getId());
            }
        });



    }

    private void deleteStatement(String id) {

        progressDialog.setMessage("Deleting..");
        adminRef.child(id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(BalanceStatementActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BalanceStatementActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading");
        progressDialog.show();

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    balanceHistoryList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        BalanceHistory balance=snapshot1.getValue(BalanceHistory.class);
                        balanceHistoryList.add(balance);
                        balanceAdapter.notifyDataSetChanged();

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