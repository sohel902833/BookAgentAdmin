package com.sohel.bookagentadmin.Admin.Model;

public class ImageModel {
    String imageUrl;
    String id;
    String position;

    public ImageModel(){

    }
    public ImageModel(String imageUrl, String id, String position) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.position = position;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
