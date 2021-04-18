package com.sohel.bookagentadmin.Admin.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.Admin.Model.ImageModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReadBookAdapter extends RecyclerView.Adapter<ReadBookAdapter.MyViewHolder>{

    private Context context;
    private List<ImageModel> imageModelList;
    private  OnItemClickListner listner;
    private  String checker="null";

    public ReadBookAdapter(Context context, List<ImageModel> imageModelList) {
        this.context = context;
        this.imageModelList = imageModelList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.read_book_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageModel currentItem=imageModelList.get(position);

        Picasso.get().load(currentItem.getImageUrl()).placeholder(R.drawable.select_image).into(holder.imageView);

        if(listner!=null){
            if(position!= RecyclerView.NO_POSITION){
                listner.currentItem(position);
            }
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null){

                    if(position!= RecyclerView.NO_POSITION){
                        listner.onDelete(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageModelList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        ImageButton deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.read_ImageViewid);
            deleteButton=itemView.findViewById(R.id.read_DeleteButtonid);



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
        void onDelete(int position);
        void currentItem(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
