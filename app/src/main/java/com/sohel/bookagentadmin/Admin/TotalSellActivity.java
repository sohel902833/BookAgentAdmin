package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.R;

import java.util.Calendar;

public class TotalSellActivity extends AppCompatActivity {

    private TextView balanceTv,toDateTv,fromTv;
    private Toolbar toolbar;

    private DatabaseReference totalSellRef;
    private ProgressDialog progressDialog;

    private  int totalSell=0;
    int cYear,cMonth,cDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_sell);

        progressDialog=new ProgressDialog(this);

        toolbar=findViewById(R.id.sellAppBArid);
        setSupportActionBar(toolbar);
        this.setTitle("Total Sell");

        totalSellRef= FirebaseDatabase.getInstance().getReference().child("SendMoneyHistory");

        balanceTv=findViewById(R.id.sell_BalanceTextViewid);
        toDateTv=findViewById(R.id.sell_ToDateTextViewid);
        fromTv=findViewById(R.id.sell_FromDateTextViewid);


        Calendar calendar = Calendar.getInstance();
        cYear = calendar.get(Calendar.YEAR);
        cMonth = calendar.get(Calendar.MONTH);
        cDay = calendar.get(Calendar.DAY_OF_MONTH);


        toDateTv.setText(cDay+"/"+cMonth+"/"+cYear);
        fromTv.setText("05/03/2002");


        loadData();

    }

    private void loadData() {

        progressDialog.setMessage("Loading");
        progressDialog.show();

        totalSellRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    totalSell=0;
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        SendBalanceModel balance=snapshot1.getValue(SendBalanceModel.class);

                        if(balance.getFrom().equals("Admin")){
                            totalSell+=balance.getAmount();
                        }

                    }
                    balanceTv.setText(""+totalSell);
                    progressDialog.dismiss();
                }else{
                    balanceTv.setText("0");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

}