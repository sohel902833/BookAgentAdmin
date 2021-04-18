package com.sohel.bookagentadmin.Admin.Model;

public class ImageModel {
    String imageUrl;
    String id;

    public ImageModel(){

    }
    public ImageModel(String imageUrl, String id) {
        this.imageUrl = imageUrl;
        this.id = id;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
