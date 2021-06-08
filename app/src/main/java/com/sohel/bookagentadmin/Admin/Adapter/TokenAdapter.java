package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.Agent.Model.AccessToken;
import com.sohel.bookagentadmin.R;

import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.MyViewHolder>{

    private Context context;
    private List<AccessToken> tokenList;
    private  OnItemClickListner listner;

    public TokenAdapter(Context context, List<AccessToken> tokenList) {
        this.context = context;
        this.tokenList = tokenList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.token_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AccessToken currentItem=tokenList.get(position);

        if(!currentItem.isSealed()){
            holder.statusTv.setTextColor(Color.RED);
            holder.statusTv.setText("Status: UNUSED");
        }else{
            holder.statusTv.setText("Status: USED");
        }

        holder.serialTv.setText(""+(position+1));
        holder.agentNameTv.setText("Agent: "+currentItem.getAgentName());
        holder.userNameTv.setText("User: "+currentItem.getUserName());
        holder.tokenTv.setText("Token: "+currentItem.getToken());
        holder.dateTv.setText("Create Date: "+currentItem.getDate()+" At "+currentItem.getTime());



    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        private TextView serialTv,agentNameTv,userNameTv,tokenTv,statusTv,dateTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serialTv=itemView.findViewById(R.id.token_SerialTextViewid);
            agentNameTv=itemView.findViewById(R.id.token_fromAgentName);
            userNameTv=itemView.findViewById(R.id.token_ToUserName);
            tokenTv=itemView.findViewById(R.id.token_TokenTextViewid);
            statusTv=itemView.findViewById(R.id.token_StatusTextViewid);
            dateTv=itemView.findViewById(R.id.token_DateTextViewid);



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
                            listner.onUserViewClick(position);
                            return  true;
                        }
                        case 2:{
                            listner.onAgentViewClick(position);
                            return  true;
                        }case 3:{
                            listner.onCopyToken(position);
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
                MenuItem view_user=menu.add(Menu.NONE,1,1,"View User Profile");
                MenuItem view_agent=menu.add(Menu.NONE,2,2,"View Agent Profile");
                MenuItem cpy_token=menu.add(Menu.NONE,3,3,"Copy Token");
                view_user.setOnMenuItemClickListener(this);
                view_agent.setOnMenuItemClickListener(this);
                cpy_token.setOnMenuItemClickListener(this);
        }
    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onAgentViewClick(int position);
        void onUserViewClick(int position);
        void onCopyToken(int position);

    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
