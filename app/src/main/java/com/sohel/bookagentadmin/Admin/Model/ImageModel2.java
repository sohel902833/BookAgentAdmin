package com.sohel.bookagentadmin.Admin.Model;

public class ImageModel2 {
    String imageUrl,id;

    public ImageModel2(){

    }
    public ImageModel2(String imageUrl,String id) {
        this.imageUrl = imageUrl;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
