package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.sohel.bookagentadmin.Admin.Model.Users;
import com.sohel.bookagentadmin.Agent.ChatingActivity;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestUserAdapter extends RecyclerView.Adapter<RequestUserAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<String> requestKey;
    private List<Users> usersList;
    private  OnItemClickListner listner;
    private DatabaseReference userRef;

    public RequestUserAdapter(Context context, ArrayList<String> requestKey, List<Users> usersList) {
        this.context = context;
        this.requestKey = requestKey;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String userId=requestKey.get(position);
        Users currentUser= getSpecificUser(userId);

        holder.nameTv.setText(currentUser.getName());
        Picasso.get().load(currentUser.getImage()).placeholder(R.drawable.profile)
                .into(holder.profileImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatingActivity.class);
                intent.putExtra("userId",currentUser.getUid());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return requestKey.size();
    }

    public Users getSpecificUser(String id){
        Users selectedUser=null;
        for(Users user:usersList){
            if(user.getUid().equals(id)){
                selectedUser=user;
                 break;
            }
        }
        return  selectedUser;
    }


    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        CircleImageView profileImage;
        TextView nameTv,lastmsgTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

                profileImage=itemView.findViewById(R.id.user_profile);
                nameTv=itemView.findViewById(R.id.username);
                lastmsgTv=itemView.findViewById(R.id.lastMsg);


              itemView.setOnClickListener(this);
              itemView.setOnCreateContextMenuListener(this);



        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(listner!=null){
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:{
                            listner.onDelete(position);
                            return  true;
                        }
                        case 2:{
                            listner.onUpdate(position);
                            return  true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
              menu.setHeaderTitle("choose an action");
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete Category");
                MenuItem update=menu.add(Menu.NONE,2,2,"Update Category");
                delete.setOnMenuItemClickListener(this);
                update.setOnMenuItemClickListener(this);

        }
    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onDelete(int position);
        void onUpdate(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
