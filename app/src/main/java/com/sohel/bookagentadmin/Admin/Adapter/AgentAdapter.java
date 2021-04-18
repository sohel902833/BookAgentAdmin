package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.MyViewHolder>{

    private Context context;
    private List<Agent> agentList;
    private  OnItemClickListner listner;

    public AgentAdapter(Context context, List<Agent> agentList) {
        this.context = context;
        this.agentList = agentList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.agent_item_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Agent currentItem=agentList.get(position);

        holder.nameTv.setText(currentItem.getName());
        holder.phoneTv.setText("Phone: "+currentItem.getPhone());
        holder.passwordTv.setText("Password: "+currentItem.getPassword());

        Picasso.get().load(currentItem.getProfileImage()).placeholder(R.drawable.profile).into(holder.profileImage);


    }

    @Override
    public int getItemCount() {
        return agentList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
       CircleImageView profileImage;
       TextView nameTv,phoneTv,passwordTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.agent_profile);
            nameTv=itemView.findViewById(R.id.agentName);
            phoneTv=itemView.findViewById(R.id.agentPhone);
            passwordTv=itemView.findViewById(R.id.agentPassword);



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
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete Agent");
                MenuItem update=menu.add(Menu.NONE,2,2,"Update Agent");
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
