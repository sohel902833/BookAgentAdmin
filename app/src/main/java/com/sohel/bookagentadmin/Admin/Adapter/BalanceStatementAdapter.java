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

import com.sohel.bookagentadmin.Admin.BookListActivity;
import com.sohel.bookagentadmin.Admin.Model.BalanceHistory;
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;

import java.util.List;

public class BalanceStatementAdapter extends RecyclerView.Adapter<BalanceStatementAdapter.MyViewHolder>{

    private Context context;
    private List<BalanceHistory> balanceHistoryList;
    private  OnItemClickListner listner;

    public BalanceStatementAdapter(Context context, List<BalanceHistory> balanceHistoryList) {
        this.context = context;
        this.balanceHistoryList = balanceHistoryList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.balance_statement_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BalanceHistory currentItem=balanceHistoryList.get(position);


        holder.balanceTv.setText("Balance: "+currentItem.getBalance());
        holder.dateTv.setText(currentItem.getDate()+" At "+currentItem.getTime());


    }
    @Override
    public int getItemCount() {
        return balanceHistoryList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        TextView balanceTv,dateTv;

      public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            balanceTv=itemView.findViewById(R.id.b_statement_BalanceTextViewid);
          dateTv=itemView.findViewById(R.id.b_statement_DateTimeTextViewid);



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
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete Book");
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
