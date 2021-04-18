package com.sohel.bookagentadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookagentadmin.Admin.BookCategoryActivity;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Agent.AgentMainActivity;
import com.sohel.bookagentadmin.LocalDatabase.UserShared;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt,passwordEt;
    private Button loginButton;
    private  ProgressBar progressBar;
    private UserShared userShared;
    private DatabaseReference agentRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        init();

        userShared=new UserShared(LoginActivity.this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEt.getText().toString();
                String password=passwordEt.getText().toString();

                if(email.isEmpty()){
                    showError(emailEt,"Required");
                }else if(password.isEmpty()){
                    showError(passwordEt,"Please Write Your Password");
                }else{
                    loginUser(email,password);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

       String userType= userShared.getUserType();
       if(userType.equals("admin")){
           sendUserToAdminMainActivity();
       }else if(userType.equals("agent")){
           checkAgent(userShared.getUserPhone(),userShared.getUserPassword());
       }

    }

    private void init() {
        emailEt=findViewById(R.id.login_phoneEt);
        passwordEt=findViewById(R.id.lpasswordEt);
        progressBar=findViewById(R.id.lprogressBar);
        loginButton=findViewById(R.id.loginButton);
    }
    public void showError(EditText editText,String message){
        editText.setError(message);
        editText.requestFocus();
        return;
    }

    private void loginUser(String email,String password) {
        progressBar.setVisibility(View.VISIBLE);
        if(email.equals("sohel@gmail.com") && password.equals("01740244739")){
            loginAdmin(email,password);
        }else{
            checkAgent(email,password);
        }
    }

    private void checkAgent(String email, String password) {
        agentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                   for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                       Agent agent=dataSnapshot1.getValue(Agent.class);
                       if(agent.getPhone().equals(email) && agent.getPassword().equals(password)){
                           loginAgent(email,password,agent.getuID());
                           break;
                       }
                   }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.warning(LoginActivity.this, "Error: ", Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void loginAgent(String phone,String password,String id) {
        userShared.saveUserData(phone,password,"agent",id);
        sendUserToAgentMainActivity();
    }

    private void sendUserToAgentMainActivity() {
        Toasty.success(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT, true).show();
        startActivity(new Intent(LoginActivity.this, AgentMainActivity.class));
        finish();
    }

    private void loginAdmin(String phone,String password) {
        userShared.saveUserData(phone,password,"admin","none");
        sendUserToAdminMainActivity();
    }

    private void sendUserToAdminMainActivity() {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

}