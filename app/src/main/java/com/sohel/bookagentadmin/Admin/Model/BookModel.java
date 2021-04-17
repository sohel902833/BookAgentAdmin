package com.sohel.bookagentadmin.Admin.Model;

import java.util.ArrayList;

public class BookModel {
    String bookName;
    String bookId;
    String categoryId;
    ArrayList<ImageModel> imageList;


    public BookModel(){

    }


    public BookModel(String bookName, String bookId, String categoryId, ArrayList<ImageModel> imageList) {
        this.bookName = bookName;
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.imageList = imageList;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
    }
}
