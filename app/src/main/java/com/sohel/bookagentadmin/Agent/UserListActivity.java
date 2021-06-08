package com.sohel.bookagentadmin.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.RequestUserAdapter;
import com.sohel.bookagentadmin.Admin.Model.RequestModel;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Agent.Adapter.UsersAdapter;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private List<Users> usersList=new ArrayList<>();

    private List<RequestModel> requestList=new ArrayList<>();




    private DatabaseReference requestRef,userRef;
    private UsersAdapter userAdapter;
    private  ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);



        requestRef= FirebaseDatabase.getInstance().getReference().child("KeyRequest");
        userRef= FirebaseDatabase.getInstance().getReference("users");


        toolbar=findViewById(R.id.user_list_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("Key Request");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        recyclerView=findViewById(R.id.user_ListRecyclerViewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter=new UsersAdapter(this,usersList);
        recyclerView.setAdapter(userAdapter);



    }
    @Override
    protected void onStart() {
        super.onStart();

        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    requestList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        RequestModel req=snapshot1.getValue(RequestModel.class);
                        requestList.add(req);
                    }
                    loadUserData(requestList);
                }else{
                    progressBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.dismiss();
            }
        });



    }

    private void loadUserData(List<RequestModel> requestList) {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                        Users user =snapshot1.getValue(Users.class);
                        if(checkExists(requestList,user.getUid())){
                           RequestModel rq= getRequestDateTime(requestList,user.getUid());
                            user.setDate(rq.getDate());
                            user.setTime(rq.getTime());
                            usersList.add(user);
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                    progressBar.dismiss();
                }else{
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean checkExists(List<RequestModel> requestList,String userId) {
       boolean isTrue=false;
        for(int i=0; i<requestList.size(); i++){
            RequestModel current=requestList.get(i);
            if(current.getUid().equals(userId)){
               isTrue=true;
               break;
            }
        }
        return isTrue;
    }
    private RequestModel getRequestDateTime(List<RequestModel> requestList,String userId) {
      RequestModel requestModel=null;
        for(int i=0; i<requestList.size(); i++){
            RequestModel current=requestList.get(i);
            if(current.getUid().equals(userId)){
               requestModel=requestList.get(i);
               break;
            }
        }
        return requestModel;
    }
}