package com.sohel.bookagentadmin.Admin.Model;

public class BookCategory {
    String categoryName;
    String image;
    String id;

    public BookCategory(){

    }

    public BookCategory(String categoryName, String image, String id) {
        this.categoryName = categoryName;
        this.image = image;
        this.id = id;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
