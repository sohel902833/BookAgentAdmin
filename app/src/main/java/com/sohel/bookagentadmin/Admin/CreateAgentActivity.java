package com.sohel.bookagentadmin.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CreateAgentActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEditText,passwordEdittext,cointEt;
    private ProgressBar progressBar;
    private Button agentCreateButton;

    private CircleImageView profileImage;



    //
    private Uri imageUri;
    private DatabaseReference agentRef,coinRef,adminRef;
    private StorageReference storageReference;

    String comeFor,pAgentId="";
    Agent agent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_agent);

        comeFor=getIntent().getStringExtra("for");
        if(comeFor.equals("update")){
            pAgentId=getIntent().getStringExtra("agentId");
        }


        adminRef= FirebaseDatabase.getInstance().getReference().child("Admin");

        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        storageReference= FirebaseStorage.getInstance().getReference().child("ProjectImage");
        coinRef= FirebaseDatabase.getInstance().getReference("AgentCoins");




        nameEdittext=findViewById(R.id.nameEt);
        phoneEditText=findViewById(R.id.phoneEt);
        passwordEdittext=findViewById(R.id.passwordEt);
        cointEt=findViewById(R.id.cointEt);

        progressBar=findViewById(R.id.progressBar);
        agentCreateButton=findViewById(R.id.createAgentButton);
        profileImage=findViewById(R.id.agentProfileId);

        if(comeFor.equals("update")){
            loadAgentData();
            cointEt.setVisibility(View.GONE);
        }

        agentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String name=nameEdittext.getText().toString();
               String phone=phoneEditText.getText().toString().trim();
               String password=passwordEdittext.getText().toString().trim();
               String coin=cointEt.getText().toString().trim();
               if(name.isEmpty()){
                   showError(nameEdittext,"Name is Required");
               }else if(phone.isEmpty()){
                   showError(phoneEditText,"Phone is required");
               }else if(password.isEmpty()){
                   showError(passwordEdittext,"Please Setup A Password for your Agent");
               }else{
                   coin=coin.isEmpty()?"0":coin;
                   int icoin=Integer.parseInt(coin);
                   if(icoin==0){
                       if(imageUri==null){
                           if(comeFor.equals("update")){
                               createAgent(phone,name,password,icoin,agent.getProfileImage());
                           }else{
                               createAgent(phone,name,password,icoin,"none");
                           }
                       }else{
                           uploadImage(name,phone,icoin,password);
                       }
                   }else{
                       checkAdminBalance(icoin,name,phone,password);
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

    private void checkAdminBalance(int entierBalance,String name,String phone,String password) {
        progressBar.setVisibility(View.VISIBLE);
        adminRef.child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("accountBalance")){
                                int adminBalance=snapshot.child("accountBalance").getValue(Integer.class);
                                if(adminBalance<entierBalance){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(CreateAgentActivity.this, "You don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(imageUri==null){
                                        if(comeFor.equals("update")){
                                            createAgent(phone,name,password,entierBalance,agent.getProfileImage());
                                        }else{
                                            createAgent(phone,name,password,entierBalance,"none");
                                        }
                                    }else{
                                        uploadImage(name,phone,entierBalance,password);
                                    }

                                }
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreateAgentActivity.this, "You Don't Have Enough Balance", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }








    private void loadAgentData() {
        agentRef.child(pAgentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                             agent=snapshot.getValue(Agent.class);
                            // getAgentCoin(agent.getuID());
                            Picasso.get().load(agent.getProfileImage()).placeholder(R.drawable.profile)
                                    .into(profileImage);
                            nameEdittext.setText(agent.getName());
                            phoneEditText.setText(agent.getPhone());
                            passwordEdittext.setText(agent.getPassword());
                            agentCreateButton.setText("Update Agent");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void getAgentCoin(String agentId) {

        coinRef.child(agentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            int coin=snapshot.child("coins").getValue(Integer.class);


                            cointEt.setText(""+coin);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
    private void uploadImage(String name,String phone,int coin,String password) {
        progressBar.setVisibility(View.VISIBLE);
        String key=agentRef.push().getKey();

        StorageReference filePath=storageReference.child(key+agentRef.push().getKey()+"."+getFileExtension(imageUri));
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloaduri=urlTask.getResult();

               createAgent(phone,name,password,coin,downloaduri.toString());


            }
        });


    }

    private void createAgent(String phone, String name, String password,int coin,String image) {

        if(comeFor.equals("update")){
            HashMap<String,Object> updateAgentMap=new HashMap<>();
            updateAgentMap.put("name",name);
            updateAgentMap.put("phone",phone);
            updateAgentMap.put("password",password);
            updateAgentMap.put("profileImage",image);

            agentRef.child(pAgentId)
                    .updateChildren(updateAgentMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                              if(coin==0){
                                  Toasty.success(CreateAgentActivity.this, "Agent Updated", Toast.LENGTH_SHORT, true).show();
                                  sendUserToAgentListActivity();
                                  finish();
                              }else{
                                 addBalanceToAgent(coin,agent.getuID());
                              }
                            } else {
                                Toasty.error(CreateAgentActivity.this, "Something Problem: " + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });

        }else {
            String agentId = agentRef.push().getKey() + System.currentTimeMillis();
            Agent ag = new Agent(agentId, name, phone, password, image);
            agentRef.child(agentId)
                    .setValue(ag)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if(coin==0){
                                    Toasty.success(CreateAgentActivity.this, "Agent Created", Toast.LENGTH_SHORT, true).show();
                                    sendUserToAgentListActivity();
                                    finish();
                                }else{
                                    addBalanceToAgent(coin,agentId);
                                }


                            } else {
                                Toasty.error(CreateAgentActivity.this, "Something Problem: " + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();

                            }
                        }
                    });
        }
    }


    private void addBalanceToAgent(int coin,String agentId) {

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("coins",coin);

        coinRef.child(agentId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toasty.success(CreateAgentActivity.this, "Agent Updated", Toast.LENGTH_SHORT, true).show();
                            sendUserToAgentListActivity();
                            finish();
                           progressBar.setVisibility(View.GONE);
                       }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreateAgentActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void sendUserToAgentListActivity() {
        startActivity(new Intent(CreateAgentActivity.this,AgentListActivity.class));
        finish();
    }


}