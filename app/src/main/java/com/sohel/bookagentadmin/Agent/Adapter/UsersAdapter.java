package com.sohel.bookagentadmin.Agent.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Agent.ChatingActivity;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {


    Context context;
    List<Users> userList;

    public UsersAdapter(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Users user=userList.get(position);


        String senderId= FirebaseAuth.getInstance().getUid();

        holder.nameTv.setText(user.getName());
        holder.lastMsgTv.setText("Request Date: "+user.getDate()+" At "+user.getTime());
        Picasso.get().load(user.getImage())
                .placeholder(R.drawable.avatar)
                .into(holder.profileImage);
        holder.phoneTv.setText(user.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatingActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("image",user.getImage());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });

        holder.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(user.getUid());
            }
        });


    }
    private void copyToClipBoard(String text) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(
                "uid", // What should I set for this "label"?
                text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Uid Copy to clip board", Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
            CircleImageView profileImage;
            TextView nameTv,lastMsgTv,phoneTv;
            private Button copyBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.user_profile);
            nameTv=itemView.findViewById(R.id.username);
            lastMsgTv=itemView.findViewById(R.id.lastMsg);
            copyBtn=itemView.findViewById(R.id.copyButtonid);
            phoneTv=itemView.findViewById(R.id.userPhone);
        }
    }
}
