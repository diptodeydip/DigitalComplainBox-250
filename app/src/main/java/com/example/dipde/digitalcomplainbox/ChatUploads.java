package com.example.dipde.digitalcomplainbox;

public class ChatUploads {
    public String des,details,imageURL;
    public ChatUploads(){

    };

    String getDes(){
        return  des;
    }
    String getDetails(){
        return details;
    }
    String getImageURL(){
        return  imageURL;
    }

    void setmDes(String des){
        this.des = des;
    }
    void setmDetails(String details){
        this.details = details;
    }
    void setmImageURL(String imageURL){
        this.imageURL = imageURL;
    }
}
