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

import com.sohel.bookagentadmin.Admin.Model.BookCategory;
import com.sohel.bookagentadmin.Admin.Model.BookModel;
import com.sohel.bookagentadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.MyViewHolder>{

    private Context context;
    private List<BookModel> bookList;
    private  OnItemClickListner listner;
    private  String checker="null";

    public BookListAdapter(Context context, List<BookModel> bookList) {
        this.context = context;
        this.bookList = bookList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.category_item_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookModel currentItem=bookList.get(position);

        holder.textView.setText(currentItem.getBookName());
        Picasso.get().load(currentItem.getImageList().get(0).getImageUrl()).placeholder(R.drawable.select_image).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

             imageView=itemView.findViewById(R.id.admin_main_categoryImageView);
             textView=itemView.findViewById(R.id.admin_main_TextViewid);

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
            if(!checker.equals("noMenu")){
                menu.setHeaderTitle("choose an action");
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete Book");
                MenuItem update=menu.add(Menu.NONE,2,2,"Update Book");
                delete.setOnMenuItemClickListener(this);
                update.setOnMenuItemClickListener(this);
            }
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
