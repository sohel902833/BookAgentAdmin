package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
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


import com.sohel.bookagentadmin.Admin.BookCategoryActivity;
import com.sohel.bookagentadmin.Admin.BookListActivity;
import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.Admin.Model.TimeDateModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookCategoryAdapter extends RecyclerView.Adapter<BookCategoryAdapter.MyViewHolder>{

    private Context context;
    private List<TimeDateModel> categoryList;
    private  OnItemClickListner listner;
    int year;

    public BookCategoryAdapter(Context context, List<TimeDateModel> categoryList,int year) {
        this.context = context;
        this.categoryList = categoryList;
        this.year=year;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.category_item_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeDateModel currentItem=categoryList.get(position);
        holder.textView.setText(currentItem.getDate()+"/"+currentItem.getMonth()+"/"+year);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, BookListActivity.class);
                intent.putExtra("day",currentItem.getDate());
                intent.putExtra("month",currentItem.getMonth());
                intent.putExtra("year",year);
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

             textView=itemView.findViewById(R.id.admin_main_TextViewid);

            itemView.setOnClickListener(this);

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
    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
