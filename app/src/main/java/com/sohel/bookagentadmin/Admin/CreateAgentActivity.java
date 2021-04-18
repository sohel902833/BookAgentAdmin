package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.R;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CreateAgentActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEditText,passwordEdittext;
    private ProgressBar progressBar;
    private Button agentCreateButton;

    private CircleImageView profileImage;



    //
    private Uri imageUri;
    private DatabaseReference agentRef;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_agent);


        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        storageReference= FirebaseStorage.getInstance().getReference().child("CategoryImages");




        nameEdittext=findViewById(R.id.nameEt);
        phoneEditText=findViewById(R.id.phoneEt);
        passwordEdittext=findViewById(R.id.passwordEt);

        progressBar=findViewById(R.id.progressBar);
        agentCreateButton=findViewById(R.id.createAgentButton);
        profileImage=findViewById(R.id.agentProfileId);


        agentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String name=nameEdittext.getText().toString();
               String phone=phoneEditText.getText().toString();
               String password=passwordEdittext.getText().toString();
               if(name.isEmpty()){
                   showError(nameEdittext,"Name is Required");
               }else if(phone.isEmpty()){
                   showError(phoneEditText,"Phone is required");
               }else if(password.isEmpty()){
                   showError(passwordEdittext,"Please Setup A Password for your Agent");
               }else{
                   if(imageUri==null){
                       createAgent(phone,name,password,"none");
                   }else{
                       uploadImage(name,phone,password);
                   }

               }



            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfilechooser();
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null){
            imageUri=data.getData();
            profileImage.setImageURI(data.getData());
        }

    }

    public void showError(EditText editText,String message){
        editText.setError(message);
        editText.requestFocus();
        return;
    }
    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }

    private void openfilechooser() {
        Intent intentf=new Intent();
        intentf.setType("image/*");
        intentf.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentf,1);
    }
    private void uploadImage(String name,String phone,String password) {
        progressBar.setVisibility(View.VISIBLE);


        String key=agentRef.push().getKey();

        StorageReference filePath=storageReference.child(key+agentRef.push().getKey()+"."+getFileExtension(imageUri));
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloaduri=urlTask.getResult();

               createAgent(phone,name,password,downloaduri.toString());


            }
        });


    }

    private void createAgent(String phone, String name, String password,String image) {
        String agentId=agentRef.push().getKey()+System.currentTimeMillis();

        Agent ag=new Agent(agentId,name,phone,password,image);
        agentRef.child(agentId)
                .setValue(ag)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toasty.success(CreateAgentActivity.this, "Agent Created", Toast.LENGTH_SHORT, true).show();
                            sendUserToAgentListActivity();
                        }else{
                            Toasty.error(CreateAgentActivity.this, "Something Problem: "+task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();

                        }
                    }
                });

    }

    private void sendUserToAgentListActivity() {
        startActivity(new Intent(CreateAgentActivity.this,AgentListActivity.class));
        finish();
    }


}