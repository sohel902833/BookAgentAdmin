package com.sohel.bookagentadmin.Admin.Model;

public class BookCategory {
    String categoryName;
    String image;
    String categoryId;

    public BookCategory(){

    }

    public BookCategory(String categoryName, String image, String categoryId) {
        this.categoryName = categoryName;
        this.image = image;
        this.categoryId = categoryId;
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
        return categoryId;
    }

    public void setId(String categoryId) {
        this.categoryId = categoryId;
    }
}
