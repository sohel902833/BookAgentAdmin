package com.sohel.bookagentadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt,passwordEt;
    private ProgressBar progressBar;
    private Button loginButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        init();


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


    private void init() {
        emailEt=findViewById(R.id.lemailEt);
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
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //login Success

                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}