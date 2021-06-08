package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.bookagentadmin.Admin.Model.Agent;
import com.sohel.bookagentadmin.Admin.Model.SendBalanceModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendMoneyAdapter extends RecyclerView.Adapter<SendMoneyAdapter.MyViewHolder>{

    private Context context;
    private List<SendBalanceModel> sendBalanceList;
    private  OnItemClickListner listner;

    public SendMoneyAdapter(Context context, List<SendBalanceModel> sendBalanceList) {
        this.context = context;
        this.sendBalanceList = sendBalanceList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.send_money_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SendBalanceModel currentItem=sendBalanceList.get(position);

        holder.fromTv.setText("From: "+currentItem.getFromName());
        holder.toTv.setText("To: "+currentItem.getToName());
         holder.serialTv.setText(""+(position+1));
        holder.dateTv.setText(currentItem.getDate()+" At "+currentItem.getTime());
        holder.balanceTv.setText("Amount: "+currentItem.getAmount());
        holder.adminNewTv.setText("Admin New Balance: "+currentItem.getAdminNew());
        holder.agentNewTv.setText("Agent New Balance: "+currentItem.getAgentNew());




    }

    @Override
    public int getItemCount() {
        return sendBalanceList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        private TextView serialTv,fromTv,toTv,agentNewTv,adminNewTv,balanceTv,dateTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serialTv=itemView.findViewById(R.id.s_money_SerialTextViewid);
            fromTv=itemView.findViewById(R.id.s_money_FromTextViewid);
            toTv=itemView.findViewById(R.id.s_money_ToTextViewid);
            agentNewTv=itemView.findViewById(R.id.s_money_AgentNewBalanceTextviewid);
            adminNewTv=itemView.findViewById(R.id.s_money_admin_NewBalance);
            balanceTv=itemView.findViewById(R.id.s_money_BalanceTextViewid);
            dateTv=itemView.findViewById(R.id.s_money_DateTimeTv);



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
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete History");
                delete.setOnMenuItemClickListener(this);
        }
    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
