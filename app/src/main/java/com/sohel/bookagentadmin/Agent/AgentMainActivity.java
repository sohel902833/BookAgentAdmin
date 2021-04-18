package com.sohel.bookagentadmin.Agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.sohel.bookagentadmin.R;

import java.util.List;

public class AgentMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_main);

        toolbar=findViewById(R.id.agentMain_AppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Key Request");

        recyclerView=findViewById(R.id.messageRequestRecyclerViewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }
}