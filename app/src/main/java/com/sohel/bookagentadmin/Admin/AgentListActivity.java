package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.Adapter.AgentAdapter;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.R;

import java.util.ArrayList;
import java.util.List;

public class AgentListActivity extends AppCompatActivity {

    private List<Agent> agentList=new ArrayList<>();
    private RecyclerView recyclerView;
    private AgentAdapter agentAdapter;
    private FloatingActionButton addAgentButton;
    private Toolbar toolbar;
    private DatabaseReference agentRef;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_list);

        toolbar=findViewById(R.id.agentListAppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("Agents");
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");

        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();


        addAgentButton=findViewById(R.id.addAgentButtonid);
        recyclerView=findViewById(R.id.agentListRecyclerviewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        agentAdapter=new AgentAdapter(this,agentList);
        recyclerView.setAdapter(agentAdapter);


        addAgentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentListActivity.this,CreateAgentActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        agentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    agentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Agent agent=snapshot1.getValue(Agent.class);
                        agentList.add(agent);
                        agentAdapter.notifyDataSetChanged();
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
}